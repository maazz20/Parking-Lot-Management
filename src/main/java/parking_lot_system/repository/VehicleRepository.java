package parking_lot_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import parking_lot_system.entity.Vehicle;

import java.util.List;
import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);

    List<Vehicle> findByColor(String color);
}