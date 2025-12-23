package com.tribly.dto.rides.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group creation request")
@ValidateSchema
public record GroupRequest(
    @Nullable @Schema(description = "id") String id,
    @Schema(description = "Group name", examples = "Fast Group", required = true)
        @NotBlank
        @Size(min = 1, max = 100)
        String name,
    @Nullable @Schema(description = "Group description") String description,
    @Nullable @Schema(description = "Average speed in km/h", examples = "25") Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants") Integer maxParticipants,
    @Nullable @Schema(description = "Route slug for this group") String routeSlug) {}
