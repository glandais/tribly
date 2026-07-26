package fr.pedalons.dto.rides.request;

import fr.pedalons.dto.validation.ValidateSchema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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
    @Nullable @Schema(description = "Average speed in km/h", examples = "25") @Positive
        Float averageSpeed,
    @Nullable @Schema(description = "Maximum participants", examples = "10") @Positive
        Integer maxParticipants,
    @Nullable @Schema(description = "Route slug for this group") String routeSlug,
    @Nullable
        @Schema(
            description =
                "ID (TSID) of the member who leads this group. Must belong to the team owning the"
                    + " ride. Omit or send null for no designated leader — clients then show no"
                    + " leader at all rather than falling back on the ride's creator.",
            examples = "0h4a8xzk8jv80")
        String leaderId) {

  /**
   * The shape this record had before group leaders existed, kept so the thirty-odd call sites that
   * build one positionally keep compiling. Same convention as the arity-preserving overloads on
   * {@code PublicationService}. New code should use the builder.
   */
  public GroupRequest(
      @Nullable String id,
      String name,
      @Nullable LocalTime time,
      @Nullable Float averageSpeed,
      @Nullable Integer maxParticipants,
      @Nullable String routeSlug) {
    this(id, name, time, averageSpeed, maxParticipants, routeSlug, null);
  }
}
