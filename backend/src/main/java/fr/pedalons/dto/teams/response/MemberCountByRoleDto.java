package fr.pedalons.dto.teams.response;

import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
import java.util.Map;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/** How a team's members split across the three roles — the administration panel's stacked bar. */
@Schema(
    description =
        "Number of members of the team per role. The three figures add up to the team's"
            + " memberCount.")
@ValidateSchema
public record MemberCountByRoleDto(
    @Schema(description = "Members with the ADMIN role", required = true) long admins,
    @Schema(description = "Members with the ORGANIZER role", required = true) long organizers,
    @Schema(description = "Members with the MEMBER role", required = true) long members) {

  public static MemberCountByRoleDto from(Map<TeamRole, Long> counts) {
    return new MemberCountByRoleDto(
        counts.getOrDefault(TeamRole.ADMIN, 0L),
        counts.getOrDefault(TeamRole.ORGANIZER, 0L),
        counts.getOrDefault(TeamRole.MEMBER, 0L));
  }
}
