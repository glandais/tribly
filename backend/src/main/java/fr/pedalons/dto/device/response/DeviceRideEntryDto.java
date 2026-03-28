package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route entry within a ride for device applications")
@Builder
@ValidateSchema
public record DeviceRideEntryDto(
    @Schema(description = "Route slug", required = true) String routeSlug,
    @Schema(description = "Route name", required = true) String routeName,
    @Schema(description = "Group name (null for ride-level route)") @Nullable String groupName,
    @Schema(description = "Distance in meters", required = true) float distance,
    @Schema(description = "Elevation gain in meters", required = true) float elevationGain,
    @Schema(description = "Start latitude") @Nullable Double startLat,
    @Schema(description = "Start longitude") @Nullable Double startLon) {}
