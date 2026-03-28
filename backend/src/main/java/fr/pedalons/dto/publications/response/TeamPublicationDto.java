package fr.pedalons.dto.publications.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.Visibility;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Team information")
@ValidateSchema
public record TeamPublicationDto(
    @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "Team URL slug", required = true) String slug,
    @Schema(description = "Whether the team is public", required = true) Visibility visibility) {
  public static TeamPublicationDto from(Team team) {
    return new TeamPublicationDto(
        TsidUtils.toString(team.getId()), team.getName(), team.getSlug(), team.getVisibility());
  }
}
