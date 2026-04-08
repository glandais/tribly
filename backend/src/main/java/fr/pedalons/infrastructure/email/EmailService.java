package fr.pedalons.infrastructure.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class EmailService {

  public static final String EMAIL_VERIFICATION = "email-verification";
  public static final String OTP = "otp";
  public static final String PASSWORD_RESET = "password-reset";

  @ConfigProperty(name = "pedalons.email.brevo.enabled", defaultValue = "false")
  boolean brevoEnabled;

  @ConfigProperty(name = "pedalons.email.brevo.api-key")
  Optional<String> brevoApiKey;

  @ConfigProperty(name = "pedalons.email.brevo.templates.email-verification.fr")
  Optional<Long> templateEmailVerificationFr;

  @ConfigProperty(name = "pedalons.email.brevo.templates.email-verification.en")
  Optional<Long> templateEmailVerificationEn;

  @ConfigProperty(name = "pedalons.email.brevo.templates.otp.fr")
  Optional<Long> templateOtpFr;

  @ConfigProperty(name = "pedalons.email.brevo.templates.otp.en")
  Optional<Long> templateOtpEn;

  @ConfigProperty(name = "pedalons.email.brevo.templates.password-reset.fr")
  Optional<Long> templatePasswordResetFr;

  @ConfigProperty(name = "pedalons.email.brevo.templates.password-reset.en")
  Optional<Long> templatePasswordResetEn;

  @Inject @RestClient BrevoRestClient brevoRestClient;

  @Inject Mailer mailer;

  public void sendEmail(
      String toEmail, String templateName, String language, Map<String, Object> params) {
    if (brevoEnabled) {
      sendViaBrevo(toEmail, templateName, language, params);
    } else {
      sendViaSMTP(toEmail, templateName, params);
    }
  }

  private void sendViaBrevo(
      String toEmail, String templateName, String language, Map<String, Object> params) {
    long templateId = resolveTemplateId(templateName, language);
    String apiKey =
        brevoApiKey.orElseThrow(() -> new IllegalStateException("Brevo API key not configured"));
    brevoRestClient.send(
        apiKey,
        new BrevoEmailRequest(
            List.of(new BrevoEmailRequest.EmailAddress(toEmail)), templateId, params));
  }

  private long resolveTemplateId(String templateName, String language) {
    return switch (templateName + "." + language) {
      case EMAIL_VERIFICATION + ".fr" ->
          templateEmailVerificationFr.orElseThrow(
              () ->
                  new IllegalStateException(
                      "Brevo template ID not configured for email-verification.fr"));
      case EMAIL_VERIFICATION + ".en" ->
          templateEmailVerificationEn.orElseThrow(
              () ->
                  new IllegalStateException(
                      "Brevo template ID not configured for email-verification.en"));
      case OTP + ".fr" ->
          templateOtpFr.orElseThrow(
              () -> new IllegalStateException("Brevo template ID not configured for otp.fr"));
      case OTP + ".en" ->
          templateOtpEn.orElseThrow(
              () -> new IllegalStateException("Brevo template ID not configured for otp.en"));
      case PASSWORD_RESET + ".fr" ->
          templatePasswordResetFr.orElseThrow(
              () ->
                  new IllegalStateException(
                      "Brevo template ID not configured for password-reset.fr"));
      case PASSWORD_RESET + ".en" ->
          templatePasswordResetEn.orElseThrow(
              () ->
                  new IllegalStateException(
                      "Brevo template ID not configured for password-reset.en"));
      default ->
          throw new IllegalArgumentException(
              "Unknown template: " + templateName + " / " + language);
    };
  }

  private void sendViaSMTP(String toEmail, String templateName, Map<String, Object> params) {
    String subject;
    String body;
    switch (templateName) {
      case EMAIL_VERIFICATION -> {
        String displayName = (String) params.get("displayName");
        String appName = (String) params.get("appName");
        String verifyUrl = (String) params.get("verifyUrl");
        subject = "Confirmez votre adresse email - " + appName;
        body =
            """
            Bonjour %s,

            Bienvenue sur %s ! Veuillez confirmer votre adresse email en cliquant sur le lien ci-dessous :

            %s

            Ce lien expirera dans 24 heures.

            Si vous n'avez pas créé de compte, vous pouvez ignorer cet email.

            Cordialement,
            L'équipe %s
            """
                .formatted(displayName, appName, verifyUrl, appName);
      }
      case OTP -> {
        String appName = (String) params.get("appName");
        String code = (String) params.get("code");
        subject = "Votre code de connexion - " + appName;
        body =
            """
            Bonjour,

            Votre code de connexion à %s est :

                %s

            Ce code expire dans 5 minutes et ne peut être utilisé qu'une seule fois.

            Si vous n'avez pas demandé ce code, vous pouvez ignorer cet email.

            Cordialement,
            L'équipe %s
            """
                .formatted(appName, code, appName);
      }
      case PASSWORD_RESET -> {
        String appName = (String) params.get("appName");
        String resetUrl = (String) params.get("resetUrl");
        subject = "Réinitialisation de votre mot de passe - " + appName;
        body =
            """
            Bonjour,

            Vous avez demandé la réinitialisation de votre mot de passe pour %s. \
            Cliquez sur le lien ci-dessous pour choisir un nouveau mot de passe :

            %s

            Ce lien expire dans 1 heure et ne peut être utilisé qu'une seule fois.

            Si vous n'avez pas demandé cette réinitialisation, vous pouvez ignorer cet email.

            Cordialement,
            L'équipe %s
            """
                .formatted(appName, resetUrl, appName);
      }
      default -> throw new IllegalArgumentException("Unknown template: " + templateName);
    }
    mailer.send(Mail.withText(toEmail, subject, body));
  }
}
