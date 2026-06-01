package parking_lot_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import parking_lot_system.entity.ParkingTicket;
import parking_lot_system.entity.TicketStatus;

import java.util.Optional;

public interface ParkingTicketRepository
        extends JpaRepository<ParkingTicket, Long> {

    Optional<ParkingTicket>
    findByVehicleRegistrationNumberAndStatus(
            String registrationNumber,
            TicketStatus status
    );
}