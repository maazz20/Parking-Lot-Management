package parking_lot_system.service;

import java.util.List;

import parking_lot_system.dto.CreateParkingLotRequest;
import parking_lot_system.dto.ParkingLotSummaryResponse;
import parking_lot_system.dto.ParkVehicleRequest;
import parking_lot_system.dto.ParkingStatusResponse;
import parking_lot_system.dto.SlotResponse;
import parking_lot_system.entity.ParkingLot;

public interface ParkingLotService {

    ParkingLot createParkingLot(CreateParkingLotRequest request);

    ParkingLotSummaryResponse getParkingLotSummary();

    String parkVehicle(ParkVehicleRequest request);

    String leaveVehicle(Integer slotNumber);

    List<ParkingStatusResponse> getParkingStatus();

    
    
    SlotResponse findSlotByRegistrationNumber(
        String registrationNumber);
        
        
    List<String> findRegistrationsByColor(String color);

    List<Integer> findSlotNumbersByColor(String color);
   
}
