package parking_lot_system.controller;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import parking_lot_system.dto.CreateParkingLotRequest;
import parking_lot_system.dto.ParkVehicleRequest;
import parking_lot_system.dto.ParkingStatusResponse;
import parking_lot_system.dto.SlotResponse;
import parking_lot_system.entity.ParkingLot;
import parking_lot_system.service.ParkingLotService;

@RestController
@RequestMapping("/api/parking-lots")
@RequiredArgsConstructor
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(
            @Valid @RequestBody CreateParkingLotRequest request) {

        ParkingLot parkingLot =
                parkingLotService.createParkingLot(request);

        return ResponseEntity.ok(parkingLot);
    }

    @PostMapping("/park")
    public ResponseEntity<String> parkVehicle(
            @Valid @RequestBody ParkVehicleRequest request) {

        return ResponseEntity.ok(
                parkingLotService.parkVehicle(request)
        );
    }

    @PostMapping("/leave/{slotNumber}")
    public ResponseEntity<String> leaveVehicle(
            @PathVariable Integer slotNumber) {

        return ResponseEntity.ok(
                parkingLotService.leaveVehicle(slotNumber)
        );
    }


    @GetMapping("/status")
    public ResponseEntity<List<ParkingStatusResponse>> getStatus() {

        return ResponseEntity.ok(
                parkingLotService.getParkingStatus()
        );
    }


    @GetMapping("/registration/{registrationNumber}")
    public ResponseEntity<SlotResponse>
    findSlotByRegistrationNumber(
            @PathVariable String registrationNumber) {

        return ResponseEntity.ok(
                parkingLotService
                        .findSlotByRegistrationNumber(
                                registrationNumber)
        );
    }


    @GetMapping("/color/{color}")
    public ResponseEntity<List<String>>
    findRegistrationsByColor(
            @PathVariable String color) {

        return ResponseEntity.ok(
                parkingLotService
                        .findRegistrationsByColor(color)
        );
    }


    @GetMapping("/color/{color}/slots")
    public ResponseEntity<List<Integer>>
    findSlotNumbersByColor(
            @PathVariable String color) {

        return ResponseEntity.ok(
                parkingLotService
                        .findSlotNumbersByColor(color)
        );
    }
}