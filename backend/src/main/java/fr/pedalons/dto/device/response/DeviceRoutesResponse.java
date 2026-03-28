package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing rides and routes for device applications")
@Builder
@ValidateSchema
public record DeviceRoutesResponse(
    @Schema(description = "Upcoming rides with route entries", required = true)
        List<DeviceRideDto> rides,
    @Schema(description = "Latest standalone routes", required = true)
        List<DeviceRouteDto> routes) {}
