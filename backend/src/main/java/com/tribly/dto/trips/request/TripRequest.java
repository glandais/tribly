package com.tribly.dto.trips.request;

import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip request")
@ValidateSchema
public record TripRequest(
    @Schema(description = "Trip name", examples = "Summer Alps Tour", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        String name,
    @Schema(description = "Trip media", required = true) MediaDto media,
    @Schema(description = "Trip start date/time", required = true) Instant dateTime,
    @Schema(description = "Trip status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Overall route slug for the trip") String routeSlug,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt,
    @Schema(description = "Trip stages to create", required = true)
        List<@Valid StageRequest> stages)
    implements WithVisibility {}
