package fr.pedalons.dto.teams.response;

import fr.pedalons.domain.team.TeamInvitation;
import fr.pedalons.dto.validation.ValidateSchema;
import fr.pedalons.enums.InvitationStatus;
import fr.pedalons.enums.TeamRole;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * What the acceptance screen may show before anyone has signed in.
 *
 * <p>The point of it being public is that an invitation landing on a signed-out browser has to be
 * able to say who is inviting and to what, or the page can only offer a bare login form with no
 * explanation. Nothing here goes beyond what the invitation e-mail already said, and only whoever
 * holds the token can ask.
 *
 * <p>The address is masked all the same. The holder of the token already knows it — it is their own
 * inbox — but a forwarded link should not hand a third party a full address for free.
 */
@Schema(description = "What an invitation says, readable by whoever holds its token")
@ValidateSchema
public record InvitationPreviewDto(
    @Schema(description = "Team name", required = true) String teamName,
    @Schema(description = "Team URL slug", required = true) String teamSlug,
    @Schema(description = "Display name of whoever sent the invitation", required = true)
        String inviterName,
    @Schema(description = "Invited address, masked (a***@example.com)", required = true)
        String maskedEmail,
    @Schema(description = "Role granted on acceptance", required = true) TeamRole role,
    @Schema(description = "Where the invitation stands", required = true) InvitationStatus status,
    @Schema(
            description =
                "Whether the invitation can still be redeemed. False for anything but a live"
                    + " PENDING one.",
            required = true)
        boolean redeemable) {

  public static InvitationPreviewDto from(TeamInvitation invitation) {
    InvitationStatus status =
        invitation.isLapsed() ? InvitationStatus.EXPIRED : invitation.getStatus();
    return new InvitationPreviewDto(
        invitation.getTeam().getName(),
        invitation.getTeam().getSlug(),
        invitation.getCreatedBy().getDisplayName(),
        maskEmail(invitation.getEmail()),
        invitation.getRole(),
        status,
        status == InvitationStatus.PENDING);
  }

  /** {@code alice@example.com} becomes {@code a***@example.com}; anything odd becomes {@code ***}. */
  static String maskEmail(String email) {
    int at = email.indexOf('@');
    if (at < 1) {
      return "***";
    }
    return email.charAt(0) + "***" + email.substring(at);
  }
}
