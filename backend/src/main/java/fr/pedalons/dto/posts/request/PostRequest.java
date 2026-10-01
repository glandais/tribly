package fr.pedalons.dto.posts.request;

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

@Schema(description = "Post request")
@ValidateSchema
public record PostRequest(
    @Schema(description = "Post name", required = true)
        @NotBlank
        @Size(min = 1, max = 200)
        @AcceptableText
        String name,
    @Schema(description = "Post description", required = true) @Valid MediaDto media,
    @Schema(description = "Post date/time", required = true) Instant dateTime,
    @Schema(description = "Post status", required = true) Status status,
    @Schema(description = "Visibility level", required = true) Visibility visibility,
    @Nullable @Schema(description = "Publication timestamp (for scheduled publishing)")
        Instant publishAt,
    @Nullable
        @Schema(
            description =
                "Sign the post as the team rather than as its author. Omitted: on creation, the"
                    + " team's postsAsTeamByDefault; on an update, left as it is.")
        Boolean signedAsTeam,
    @Nullable
        @Schema(
            description =
                "IDs (TSID) of the team's POST tags the post carries, replacing the whole set — at"
                    + " most 10, each a tag of this team and of kind POST, else 400 (TAG_INVALID,"
                    + " TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a"
                    + " creation, left as they are on an update.")
        List<String> tagIds)
    implements WithVisibility {

  /** Without tags: the shape this record had before API-59 — leaves the tags as they are. */
  public PostRequest(
      String name,
      MediaDto media,
      Instant dateTime,
      Status status,
      Visibility visibility,
      @Nullable Instant publishAt,
      @Nullable Boolean signedAsTeam) {
    this(name, media, dateTime, status, visibility, publishAt, signedAsTeam, null);
  }
}
