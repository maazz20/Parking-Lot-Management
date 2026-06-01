package parking_lot_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import parking_lot_system.entity.ParkingSlot;

import java.util.List;
import java.util.Optional;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    List<ParkingSlot> findByOccupied(boolean occupied);

    ParkingSlot findBySlotNumber(Integer slotNumber);

    Optional<ParkingSlot> findFirstByOccupiedFalseOrderBySlotNumberAsc();

    @Query("""
       SELECT ps
       FROM ParkingSlot ps
       WHERE ps.activeTicket.vehicle.registrationNumber = :registrationNumber
       """)
        Optional<ParkingSlot> findSlotByRegistrationNumber(
            @Param("registrationNumber") String registrationNumber);




    @Query("""
       SELECT ps
       FROM ParkingSlot ps
       WHERE ps.activeTicket.vehicle.color = :color
       """)
    List<ParkingSlot> findSlotsByVehicleColor(
            @Param("color") String color);
}