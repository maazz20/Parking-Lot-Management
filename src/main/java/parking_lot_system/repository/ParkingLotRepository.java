package parking_lot_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import parking_lot_system.entity.ParkingLot;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {
}