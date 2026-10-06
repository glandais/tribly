package fr.pedalons.dto.device.response;

import fr.pedalons.dto.validation.ValidateSchema;
import java.time.Instant;
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
    @Schema(description = "Start longitude") @Nullable Double startLon,
    @Schema(
            description =
                "When this entry leaves, as an absolute instant (UTC): the group's startAt (its"
                    + " time on the ride's local date, in the ride's zone); the ride's own"
                    + " startDateTime for the ride-level route and for a group without a time."
                    + " Devices render it in their own zone.",
            required = true)
        Instant startDateTime) {}
