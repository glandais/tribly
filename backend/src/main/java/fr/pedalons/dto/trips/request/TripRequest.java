package fr.pedalons.dto.trips.request;

import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.WithVisibility;
import fr.pedalons.dto.validation.AcceptableText;
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

@Schema(description = "Trip request")
@ValidateSchema
public record TripRequest(
    @Schema(description = "Trip name", examples = "Summer Alps Tour", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        @AcceptableText
        String name,
    @Schema(description = "Trip media", required = true) @Valid MediaDto media,
    @Schema(description = "Trip start date/time", required = true) Instant dateTime,
    @Schema(description = "Trip status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Overall route slug for the trip") String routeSlug,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt,
    @Schema(description = "Trip stages to create", required = true)
        List<@Valid StageRequest> stages,
    @Nullable
        @Schema(
            description =
                "IDs (TSID) of the team's TRIP tags the trip carries, replacing the whole set — at"
                    + " most 10, each a tag of this team and of kind TRIP, else 400 (TAG_INVALID,"
                    + " TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a"
                    + " creation, left as they are on an update.")
        List<String> tagIds)
    implements WithVisibility {

  /** Without tags: the shape this record had before API-59 — leaves the tags as they are. */
  public TripRequest(
      String name,
      MediaDto media,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable String routeSlug,
      @Nullable Instant publishAt,
      List<StageRequest> stages) {
    this(name, media, dateTime, status, visibility, routeSlug, publishAt, stages, null);
  }
}
