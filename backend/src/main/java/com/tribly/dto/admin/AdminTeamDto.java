package com.tribly.dto.admin;

import com.tribly.common.TsidUtils;
import com.tribly.domain.team.Team;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.Visibility;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Admin team view with domain info")
@ValidateSchema
public record AdminTeamDto(
    @Schema(description = "Team ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(description = "Team name", required = true) String name,
    @Schema(description = "Team URL slug", required = true) String slug,
    @Schema(description = "Domain ID this team belongs to", required = true) String domainId,
    @Schema(description = "Domain hostname", required = true) String domainName,
    @Schema(description = "Team visibility", required = true) Visibility visibility,
    @Schema(description = "Is team soft-deleted", required = true) boolean deleted,
    @Schema(description = "Number of members", required = true) long memberCount,
    @Schema(description = "Team creation timestamp", required = true) Instant createdAt) {

  public static AdminTeamDto from(Team team, long memberCount) {
    return new AdminTeamDto(
        TsidUtils.toString(team.getId()),
        team.getName(),
        team.getSlug(),
        TsidUtils.toString(team.getDomain().getId()),
        team.getDomain().getDomain(),
        team.getVisibility(),
        team.isDeleted(),
        memberCount,
        team.getCreatedAt());
  }
}
