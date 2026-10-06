package fr.pedalons.dto.trips.request;

import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.validation.AcceptableText;
import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Builder;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Trip stage creation request")
@ValidateSchema
@Builder
public record StageRequest(
    @Nullable @Schema(description = "Stage ID (for updates)") String id,
    @Schema(description = "Stage name", examples = "Day 1 - Geneva to Chamonix", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        @AcceptableText
        String name,
    @Schema(description = "Stage date/time", required = true) Instant dateTime,
    @Nullable @Schema(description = "Average speed in km/h", examples = "22") @Positive
        Float averageSpeed,
    @Nullable @Schema(description = "Route slug for this stage") String routeSlug,
    @Nullable @Schema(description = "Start place ID (TSID)") String startPlaceId,
    @Nullable @Schema(description = "End place ID (TSID)") String endPlaceId,
    @Schema(description = "Stage media", required = true) @Valid MediaDto media) {}
