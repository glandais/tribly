package com.tribly.dto.ridetemplates.request;

import com.tribly.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride template group request")
@ValidateSchema
@Builder
public record RideTemplateGroupRequest(
    @Nullable @Schema(description = "Group ID (TSID) - only for updates") String id,
    @Schema(description = "Group name", required = true) @NotBlank @Size(min = 1, max = 200)
        String name,
    @Nullable LocalTime time,
    @Nullable @Schema(description = "Average speed in km/h", examples = "25") Integer averageSpeed,
    @Nullable @Schema(description = "Maximum participants", examples = "15")
        Integer maxParticipants) {}
