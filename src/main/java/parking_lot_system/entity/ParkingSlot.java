package parking_lot_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer slotNumber;

    private boolean occupied;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ticket_id")
    private ParkingTicket activeTicket;
}