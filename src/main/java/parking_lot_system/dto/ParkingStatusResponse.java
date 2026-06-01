package parking_lot_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingStatusResponse {

    private Integer slotNumber;

    private String registrationNumber;

    private String color;
}