package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.GpsServiceType;
import java.util.List;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Response containing user's device connection status")
@Builder
@ValidateSchema
public record DeviceUserStatusResponse(
    @Schema(description = "List of connected GPS services", required = true)
        List<GpsServiceType> connectedGpsServices) {}
