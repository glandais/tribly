package com.tribly.dto.garmin.response;

import com.tribly.dto.validation.ValidateSchema;
import java.time.Instant;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route information for Garmin device")
@Builder
@ValidateSchema
public record GarminRouteDto(
    @Schema(description = "Team slug", required = true) String teamSlug,
    @Schema(description = "Route slug", required = true) String routeSlug,
    @Schema(description = "Route name", required = true) String name,
    @Schema(description = "Label (e.g., 'Rapides - Sam 18 Jan 09:00')") @Nullable String label,
    @Schema(description = "Ride date/time if from a ride") @Nullable Instant rideDateTime,
    @Schema(description = "Distance in meters", required = true) float distance,
    @Schema(description = "Elevation gain in meters", required = true) float elevationGain,
    @Schema(description = "Start latitude") @Nullable Double startLat,
    @Schema(description = "Start longitude") @Nullable Double startLon,
    @Schema(description = "Distance from user in meters (if GPS provided)")
        @Nullable Double distanceFromUser) {}
