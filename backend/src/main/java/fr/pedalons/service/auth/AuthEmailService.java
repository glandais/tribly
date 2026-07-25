package fr.pedalons.service.auth;

import fr.pedalons.infrastructure.email.EmailService;
import fr.pedalons.infrastructure.i18n.LanguageResolver;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.ResolvedSite;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

/**
 * Auth emails are always sent from the request that triggered them, so the recipient's language
 * comes from that request — these users have no account language to read (registration, OTP and
 * password reset all happen before or without a session).
 */
@ApplicationScoped
public class AuthEmailService {

  @Inject EmailService emailService;

  @Inject DomainResolver domainResolver;

  @Inject LanguageResolver languageResolver;

  public void sendVerificationEmail(String email, String displayName, String token) {
    ResolvedSite site = domainResolver.getResolvedSite();
    String appName = site.effectiveName();
    String verifyUrl = site.effectiveBaseUrl() + "/verify-email?token=" + token;
    emailService.sendEmail(
        email,
        EmailService.EMAIL_VERIFICATION,
        languageResolver.getLanguage(),
        Map.of("displayName", displayName, "appName", appName, "verifyUrl", verifyUrl));
  }

  public void sendOtpEmail(String email, String code) {
    String appName = domainResolver.getEffectiveName();
    emailService.sendEmail(
        email,
        EmailService.OTP,
        languageResolver.getLanguage(),
        Map.of("appName", appName, "code", code));
  }

  public void sendPasswordResetEmail(String email, String token) {
    ResolvedSite site = domainResolver.getResolvedSite();
    String appName = site.effectiveName();
    String resetUrl = site.effectiveBaseUrl() + "/reset-password?token=" + token;
    emailService.sendEmail(
        email,
        EmailService.PASSWORD_RESET,
        languageResolver.getLanguage(),
        Map.of("appName", appName, "resetUrl", resetUrl));
  }
}
