package parking_lot_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateParkingLotRequest {

    @NotBlank(message = "Parking lot name is required")
    private String name;

    @Min(value = 1, message = "Total slots must be at least 1")
    private Integer totalSlots;
}