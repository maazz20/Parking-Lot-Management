package parking_lot_system.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import parking_lot_system.dto.CreateParkingLotRequest;
import parking_lot_system.dto.ParkingLotSummaryResponse;
import parking_lot_system.dto.ParkVehicleRequest;
import parking_lot_system.dto.ParkingStatusResponse;
import parking_lot_system.dto.SlotResponse;
import parking_lot_system.entity.*;
import parking_lot_system.exception.ParkingLotFullException;
import parking_lot_system.exception.SlotNotFoundException;
import parking_lot_system.exception.VehicleNotFoundException;
import parking_lot_system.repository.ParkingLotRepository;
import parking_lot_system.repository.ParkingSlotRepository;
import parking_lot_system.repository.ParkingTicketRepository;
import parking_lot_system.repository.VehicleRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingLotServiceImpl implements ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSlotRepository parkingSlotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingTicketRepository parkingTicketRepository;

    @Override
    public ParkingLot createParkingLot(CreateParkingLotRequest request) {

        List<ParkingSlot> slots = new ArrayList<>();

        for (int i = 1; i <= request.getTotalSlots(); i++) {

            ParkingSlot slot = ParkingSlot.builder()
                    .slotNumber(i)
                    .occupied(false)
                    .build();

            slots.add(slot);
        }

        ParkingLot parkingLot = ParkingLot.builder()
                .name(request.getName())
                .totalSlots(request.getTotalSlots())
                .slots(slots)
                .build();

        return parkingLotRepository.save(parkingLot);
    }

    @Override
    public ParkingLotSummaryResponse getParkingLotSummary() {

        ParkingLot latestLot = parkingLotRepository
                .findTopByOrderByIdDesc()
                .orElse(null);

        long totalSlots = parkingSlotRepository.count();
        long occupiedSlots = parkingSlotRepository.countByOccupied(true);

        return ParkingLotSummaryResponse.builder()
                .id(latestLot != null ? latestLot.getId() : null)
                .name(latestLot != null ? latestLot.getName() : null)
                .totalSlots(totalSlots)
                .occupiedSlots(occupiedSlots)
                .availableSlots(totalSlots - occupiedSlots)
                .build();
    }

    @Override
    public String parkVehicle(ParkVehicleRequest request) {

        if (parkingSlotRepository.count() == 0) {
            throw new ParkingLotFullException(
                    "Create a parking lot before parking vehicles");
        }

        ParkingSlot slot = parkingSlotRepository
                .findFirstByOccupiedFalseOrderBySlotNumberAsc()
                .orElseThrow(() ->
                        new ParkingLotFullException("Parking lot is full"));

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(request.getRegistrationNumber())
                .color(request.getColor())
                .build();

        vehicleRepository.save(vehicle);

        ParkingTicket ticket = ParkingTicket.builder()
                .vehicle(vehicle)
                .slot(slot)
                .entryTime(LocalDateTime.now())
                .status(TicketStatus.ACTIVE)
                .build();

        parkingTicketRepository.save(ticket);

        slot.setActiveTicket(ticket);
        slot.setOccupied(true);

        parkingSlotRepository.save(slot);

        return "Vehicle parked at slot "
                + slot.getSlotNumber()
                + " | Ticket ID: "
                + ticket.getId();
    }

    @Override
    public String leaveVehicle(Integer slotNumber) {

        ParkingSlot slot =
                parkingSlotRepository.findBySlotNumber(slotNumber);

        if (slot == null) {
            throw new SlotNotFoundException("Slot not found");
        }

        if (!slot.isOccupied()) {
            throw new IllegalStateException("Slot is already empty");
        }

        ParkingTicket ticket = slot.getActiveTicket();

        ticket.setExitTime(LocalDateTime.now());

        long hours = Duration.between(
                ticket.getEntryTime(),
                ticket.getExitTime()
        ).toHours();

        if (hours == 0) {
            hours = 1;
        }

        double fee = hours * 20.0;

        ticket.setFee(fee);
        ticket.setStatus(TicketStatus.COMPLETED);

        parkingTicketRepository.save(ticket);

        slot.setOccupied(false);
        slot.setActiveTicket(null);

        parkingSlotRepository.save(slot);

        return "Vehicle exited. Parking Fee = ₹" + fee;
    }

    @Override
    public List<ParkingStatusResponse> getParkingStatus() {

        List<ParkingSlot> occupiedSlots =
                parkingSlotRepository.findByOccupied(true);

        return occupiedSlots.stream()
                .map(slot -> ParkingStatusResponse.builder()
                        .slotNumber(slot.getSlotNumber())
                        .registrationNumber(
                                slot.getActiveTicket()
                                        .getVehicle()
                                        .getRegistrationNumber())
                        .color(
                                slot.getActiveTicket()
                                        .getVehicle()
                                        .getColor())
                        .build())
                .toList();
    }

    @Override
    public SlotResponse findSlotByRegistrationNumber(
            String registrationNumber) {

        ParkingSlot slot = parkingSlotRepository
                .findSlotByRegistrationNumber(registrationNumber)
                .orElseThrow(() ->
                        new VehicleNotFoundException("Vehicle not found"));

        return new SlotResponse(
                slot.getSlotNumber()
        );
    }

    @Override
    public List<String> findRegistrationsByColor(String color) {

        return vehicleRepository.findByColor(color)
                .stream()
                .map(Vehicle::getRegistrationNumber)
                .toList();
    }

    @Override
    public List<Integer> findSlotNumbersByColor(String color) {

        return parkingSlotRepository
                .findSlotsByVehicleColor(color)
                .stream()
                .map(ParkingSlot::getSlotNumber)
                .toList();
    }
}
