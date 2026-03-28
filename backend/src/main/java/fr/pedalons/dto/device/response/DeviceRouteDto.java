package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Standalone route information for device applications")
@Builder
@ValidateSchema
public record DeviceRouteDto(
    @Schema(description = "Team slug", required = true) String teamSlug,
    @Schema(description = "Route slug", required = true) String routeSlug,
    @Schema(description = "Route name", required = true) String routeName,
    @Schema(description = "Distance in meters", required = true) float distance,
    @Schema(description = "Elevation gain in meters", required = true) float elevationGain,
    @Schema(description = "Start latitude") @Nullable Double startLat,
    @Schema(description = "Start longitude") @Nullable Double startLon) {}
