package parking_lot_system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ParkVehicleRequest {

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Color is required")
    private String color;
}