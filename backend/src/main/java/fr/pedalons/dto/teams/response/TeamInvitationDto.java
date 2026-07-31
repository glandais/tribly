package fr.pedalons.dto.teams.response;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.team.TeamInvitation;
import fr.pedalons.dto.users.response.PublicUserDto;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.InvitationStatus;
import fr.pedalons.enums.TeamRole;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

/** An invitation as its team's administrators see it — address in the clear, since they typed it. */
@Schema(description = "A pending or settled invitation to join a team")
@ValidateSchema
public record TeamInvitationDto(
    @Schema(description = "Invitation ID (TSID)", required = true) String id,
    @Schema(description = "Invited e-mail address", required = true) String email,
    @Schema(description = "Role granted on acceptance", required = true) TeamRole role,
    @Schema(description = "Where the invitation stands", required = true) InvitationStatus status,
    @Schema(description = "When the invitation stops being redeemable", required = true)
        Instant expiresAt,
    @Schema(description = "When the invitation was sent", required = true) Instant createdAt,
    @Schema(description = "Who sent it", required = true) PublicUserDto invitedBy,
    @Nullable @Schema(description = "When it was accepted, if it was") Instant acceptedAt) {

  public static TeamInvitationDto from(TeamInvitation invitation) {
    return new TeamInvitationDto(
        TsidUtils.toString(invitation.getId()),
        invitation.getEmail(),
        invitation.getRole(),
        // A row the nightly sweep has not reached yet reads EXPIRED here rather than PENDING: the
        // admin should see the same state the accept call would enforce.
        invitation.isLapsed() ? InvitationStatus.EXPIRED : invitation.getStatus(),
        invitation.getExpiresAt(),
        invitation.getCreatedAt(),
        PublicUserDto.from(invitation.getCreatedBy()),
        invitation.getAcceptedAt());
  }
}
