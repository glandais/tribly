package com.tribly.dto.device.response;

import com.tribly.dto.validation.ValidateSchema;
import java.time.Instant;
import java.time.LocalTime;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route information for device applications")
@Builder
@ValidateSchema
public record DeviceRouteDto(
    @Schema(description = "Team slug", required = true) String teamSlug,
    @Schema(description = "Route slug", required = true) String routeSlug,
    @Schema(description = "Route name", required = true) String routeName,
    @Schema(description = "Ride name") @Nullable String rideName,
    @Schema(description = "Ride date/time") @Nullable Instant rideDateTime,
    @Schema(description = "Group name") @Nullable String groupName,
    @Schema(description = "Group time") @Nullable LocalTime groupTime,
    @Schema(description = "Distance in meters", required = true) float distance,
    @Schema(description = "Elevation gain in meters", required = true) float elevationGain,
    @Schema(description = "Start latitude", required = true) Double startLat,
    @Schema(description = "Start longitude", required = true) Double startLon) {}
