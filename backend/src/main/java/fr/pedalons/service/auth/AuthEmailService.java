package fr.pedalons.service.auth;

import fr.pedalons.infrastructure.email.EmailService;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.ResolvedSite;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class AuthEmailService {

  @Inject EmailService emailService;

  @Inject DomainResolver domainResolver;

  public void sendVerificationEmail(String email, String displayName, String token) {
    ResolvedSite site = domainResolver.getResolvedSite();
    String appName = site.effectiveName();
    String verifyUrl = site.effectiveBaseUrl() + "/verify-email?token=" + token;
    emailService.sendEmail(
        email,
        EmailService.EMAIL_VERIFICATION,
        "fr",
        Map.of("displayName", displayName, "appName", appName, "verifyUrl", verifyUrl));
  }

  public void sendOtpEmail(String email, String code) {
    String appName = domainResolver.getEffectiveName();
    emailService.sendEmail(email, EmailService.OTP, "fr", Map.of("appName", appName, "code", code));
  }

  public void sendPasswordResetEmail(String email, String token) {
    ResolvedSite site = domainResolver.getResolvedSite();
    String appName = site.effectiveName();
    String resetUrl = site.effectiveBaseUrl() + "/reset-password?token=" + token;
    emailService.sendEmail(
        email, EmailService.PASSWORD_RESET, "fr", Map.of("appName", appName, "resetUrl", resetUrl));
  }
}
