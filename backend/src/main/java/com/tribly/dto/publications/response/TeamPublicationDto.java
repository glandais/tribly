package com.tribly.dto.publications.response;

import com.tribly.common.TsidUtils;
import com.tribly.domain.team.Team;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
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
