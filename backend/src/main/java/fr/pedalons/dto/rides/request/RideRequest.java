package fr.pedalons.dto.rides.request;

import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.WithVisibility;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Ride request")
@ValidateSchema
public record RideRequest(
    @Schema(description = "Ride name", examples = "Sunday Morning Ride", required = true)
        @NotBlank
        @Size(min = 3, max = 200)
        String name,
    @Schema(description = "Ride media", required = true) @Valid MediaDto media,
    @Schema(description = "Ride date/time", required = true) Instant dateTime,
    @Schema(description = "Ride status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Route slug") String routeSlug,
    @Nullable @Schema(description = "Start place ID (TSID)") String startPlaceId,
    @Nullable @Schema(description = "End place ID (TSID)") String endPlaceId,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt,
    @Schema(description = "Ride groups to create", required = true)
        List<@Valid GroupRequest> groups)
    implements WithVisibility {}
