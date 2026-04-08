package fr.pedalons.service.auth;

import fr.pedalons.domain.platform.Domain;
import fr.pedalons.infrastructure.email.EmailService;
import fr.pedalons.service.security.DomainResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;

@ApplicationScoped
public class AuthEmailService {

  @Inject EmailService emailService;

  @Inject DomainResolver domainResolver;

  public void sendVerificationEmail(String email, String displayName, String token) {
    Domain domain = domainResolver.getDomain();
    String appName = domain.getName();
    String verifyUrl = domain.getBaseUrl() + "/verify-email?token=" + token;
    emailService.sendEmail(
        email,
        EmailService.EMAIL_VERIFICATION,
        "fr",
        Map.of("displayName", displayName, "appName", appName, "verifyUrl", verifyUrl));
  }

  public void sendOtpEmail(String email, String code) {
    Domain domain = domainResolver.getDomain();
    String appName = domain.getName();
    emailService.sendEmail(email, EmailService.OTP, "fr", Map.of("appName", appName, "code", code));
  }

  public void sendPasswordResetEmail(String email, String token) {
    Domain domain = domainResolver.getDomain();
    String appName = domain.getName();
    String resetUrl = domain.getBaseUrl() + "/reset-password?token=" + token;
    emailService.sendEmail(
        email, EmailService.PASSWORD_RESET, "fr", Map.of("appName", appName, "resetUrl", resetUrl));
  }
}
