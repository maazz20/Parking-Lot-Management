package parking_lot_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingLot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer totalSlots;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "parking_lot_id")
    private List<ParkingSlot> slots;
}