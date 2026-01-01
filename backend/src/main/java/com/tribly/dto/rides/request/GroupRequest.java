package com.tribly.dto.rides.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride group creation request")
@ValidateSchema
@Builder
public record GroupRequest(
    @Nullable @Schema(description = "id") String id,
    @Schema(description = "Group name", examples = "Fast Group", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        String name,
    @Nullable LocalTime time,
    @Nullable @Schema(description = "Average speed in km/h", examples = "25") Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants") Integer maxParticipants,
    @Nullable @Schema(description = "Route slug for this group") String routeSlug) {}
