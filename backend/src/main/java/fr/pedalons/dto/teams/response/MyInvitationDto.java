package fr.pedalons.dto.teams.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.team.TeamInvitation;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.TeamRole;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * An invitation as its recipient sees it, once they have an account.
 *
 * <p>This is what catches the case nothing else does: someone invited before they had an account,
 * who then signs up through the ordinary form instead of clicking the link. No membership is ever
 * created automatically — signing up is not the same act as joining a team, and with two invitations
 * waiting there would be no way to guess which one was meant — so the invitations wait here until
 * they are accepted or they lapse.
 */
@Schema(description = "An invitation waiting for the current user")
@ValidateSchema
public record MyInvitationDto(
    @Schema(description = "Invitation ID (TSID)", required = true) String id,
    @Schema(description = "Team being offered", required = true) TeamPublicationDto team,
    @Schema(description = "Display name of whoever sent it", required = true) String inviterName,
    @Schema(description = "Role granted on acceptance", required = true) TeamRole role,
    @Schema(description = "When it stops being redeemable", required = true) Instant expiresAt) {

  public static MyInvitationDto from(TeamInvitation invitation) {
    return new MyInvitationDto(
        TsidUtils.toString(invitation.getId()),
        TeamPublicationDto.from(invitation.getTeam()),
        invitation.getCreatedBy().getDisplayName(),
        invitation.getRole(),
        invitation.getExpiresAt());
  }
}
