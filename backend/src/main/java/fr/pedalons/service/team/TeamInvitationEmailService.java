package fr.pedalons.service.team;

import fr.pedalons.domain.team.TeamInvitation;
import fr.pedalons.domain.user.User;
import fr.pedalons.infrastructure.email.EmailService;
import fr.pedalons.infrastructure.i18n.LanguageResolver;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.ResolvedSite;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * Builds and sends "someone invites you to join their team".
 *
 * <p>Two templates, one URL. Which template is chosen is the only thing that betrays whether an
 * account exists, and it betrays it to the invitee alone — who knows anyway. The API response is
 * identical either way; see {@link TeamInvitationService}.
 *
 * <p>The link points at the same page in both cases. That page copes with all three states — signed
 * in, signed out with an account, signed out without one — so pointing the second mail at a sign-up
 * page would have been both redundant and impossible: {@code contracts/routes.yaml} marks
 * {@code register} as mobile-only, the web has no dedicated sign-up route.
 */
@ApplicationScoped
public class TeamInvitationEmailService {

  @Inject EmailService emailService;

  @Inject DomainResolver domainResolver;

  @Inject LanguageResolver languageResolver;

  /**
   * @param recipient the invitee's account, when they have one. Null is the whole difference between
   *     the two templates.
   */
  public void sendInvitation(TeamInvitation invitation, @Nullable User recipient, String token) {
    ResolvedSite site = domainResolver.getResolvedSite();
    // Query string, not a path segment: the token is a bearer secret, and paths end up in access
    // logs and in the Referer of anything the page loads. Same shape as /verify-email?token= and
    // /reset-password?token=.
    String invitationUrl = "%s/invitation?token=%s".formatted(site.effectiveBaseUrl(), token);

    long days = Math.max(1, Duration.between(Instant.now(), invitation.getExpiresAt()).toDays());

    // Someone with an account has stated a language; someone without one has stated nothing, so the
    // only signal left is the language the inviter was browsing in. Same rule as the ad-contact
    // relay, which reads the recipient's preference rather than the sender's request.
    String language =
        recipient != null && recipient.getLanguage() != null
            ? recipient.getLanguage()
            : languageResolver.getLanguage();

    emailService.sendEmail(
        invitation.getEmail(),
        recipient != null ? EmailService.TEAM_INVITATION : EmailService.TEAM_INVITATION_SIGNUP,
        language,
        Map.of(
            "appName", site.effectiveName(),
            "inviterName", invitation.getCreatedBy().getDisplayName(),
            "teamName", invitation.getTeam().getName(),
            "invitationUrl", invitationUrl,
            // The role is deliberately absent: translating it would mean a second copy of the
            // roles.* table in Java, in two languages. The acceptance screen shows it, translated
            // by the client that already owns that table.
            "expiresInDays", String.valueOf(days)));
  }
}
