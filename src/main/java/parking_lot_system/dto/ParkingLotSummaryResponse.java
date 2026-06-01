package parking_lot_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingLotSummaryResponse {

    private Long id;

    private String name;

    private long totalSlots;

    private long occupiedSlots;

    private long availableSlots;
}
