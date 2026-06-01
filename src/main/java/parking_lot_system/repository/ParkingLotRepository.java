package parking_lot_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import parking_lot_system.entity.ParkingLot;

import java.util.Optional;

public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    Optional<ParkingLot> findTopByOrderByIdDesc();
}
