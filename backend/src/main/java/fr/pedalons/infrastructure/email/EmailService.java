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
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class EmailService {

  public static final String EMAIL_VERIFICATION = "email-verification";
  public static final String OTP = "otp";
  public static final String PASSWORD_RESET = "password-reset";
  public static final String DATA_EXPORT = "data-export";
  public static final String AD_CONTACT = "ad-contact";

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

  @ConfigProperty(name = "pedalons.email.brevo.templates.data-export.fr")
  Optional<Long> templateDataExportFr;

  @ConfigProperty(name = "pedalons.email.brevo.templates.data-export.en")
  Optional<Long> templateDataExportEn;

  @ConfigProperty(name = "pedalons.email.brevo.templates.ad-contact.fr")
  Optional<Long> templateAdContactFr;

  @ConfigProperty(name = "pedalons.email.brevo.templates.ad-contact.en")
  Optional<Long> templateAdContactEn;

  @Inject @RestClient BrevoRestClient brevoRestClient;

  @Inject Mailer mailer;

  public void sendEmail(
      String toEmail, String templateName, String language, Map<String, Object> params) {
    sendEmail(toEmail, templateName, language, params, null);
  }

  /**
   * Sends a template, optionally asking replies to go somewhere other than the no-reply sender.
   *
   * <p>Every other email this service sends is a notification nobody answers, so {@code replyTo}
   * stayed null until the classified-ad relay needed it: the whole point of relaying a message is
   * that the recipient can answer the person who wrote it without either address having been
   * published.
   */
  public void sendEmail(
      String toEmail,
      String templateName,
      String language,
      Map<String, Object> params,
      @Nullable String replyTo) {
    if (brevoEnabled) {
      sendViaBrevo(toEmail, templateName, language, params, replyTo);
    } else {
      sendViaSMTP(toEmail, templateName, params, replyTo);
    }
  }

  private void sendViaBrevo(
      String toEmail,
      String templateName,
      String language,
      Map<String, Object> params,
      @Nullable String replyTo) {
    long templateId = resolveTemplateId(templateName, language);
    String apiKey =
        brevoApiKey.orElseThrow(() -> new IllegalStateException("Brevo API key not configured"));
    brevoRestClient.send(
        apiKey,
        new BrevoEmailRequest(
            List.of(new BrevoEmailRequest.EmailAddress(toEmail)),
            templateId,
            params,
            replyTo == null ? null : new BrevoEmailRequest.EmailAddress(replyTo)));
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
      case DATA_EXPORT + ".fr" ->
          templateDataExportFr.orElseThrow(
              () ->
                  new IllegalStateException("Brevo template ID not configured for data-export.fr"));
      case DATA_EXPORT + ".en" ->
          templateDataExportEn.orElseThrow(
              () ->
                  new IllegalStateException("Brevo template ID not configured for data-export.en"));
      case AD_CONTACT + ".fr" ->
          templateAdContactFr.orElseThrow(
              () ->
                  new IllegalStateException("Brevo template ID not configured for ad-contact.fr"));
      case AD_CONTACT + ".en" ->
          templateAdContactEn.orElseThrow(
              () ->
                  new IllegalStateException("Brevo template ID not configured for ad-contact.en"));
      default ->
          throw new IllegalArgumentException(
              "Unknown template: " + templateName + " / " + language);
    };
  }

  private void sendViaSMTP(
      String toEmail, String templateName, Map<String, Object> params, @Nullable String replyTo) {
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
      case DATA_EXPORT -> {
        String appName = (String) params.get("appName");
        String displayName = (String) params.get("displayName");
        String downloadUrl = (String) params.get("downloadUrl");
        String expiresAt = (String) params.get("expiresAt");
        String fileSize = (String) params.get("fileSize");
        subject = "Votre export de données est prêt - " + appName;
        body =
            """
            Bonjour %s,

            L'export de vos données personnelles %s est prêt (%s). \
            Vous pouvez le télécharger via le lien ci-dessous :

            %s

            Ce lien est personnel : toute personne qui l'obtient peut télécharger vos données. \
            Il expire le %s, après quoi le fichier est supprimé de nos serveurs et vous devrez \
            demander un nouvel export.

            Cordialement,
            L'équipe %s
            """
                .formatted(displayName, appName, fileSize, downloadUrl, expiresAt, appName);
      }
      case AD_CONTACT -> {
        String appName = (String) params.get("appName");
        String recipientName = (String) params.get("recipientName");
        String senderName = (String) params.get("senderName");
        String adName = (String) params.get("adName");
        String adUrl = (String) params.get("adUrl");
        String message = (String) params.get("message");
        subject = "%s vous écrit au sujet de « %s »".formatted(senderName, adName);
        body =
            """
            Bonjour %s,

            %s vous a écrit au sujet de votre annonce « %s » sur %s :

            %s

            Vous pouvez répondre directement à cet e-mail : votre réponse partira vers l'adresse \
            de %s. Votre propre adresse ne lui a pas été communiquée.

            L'annonce : %s

            Si vous ne souhaitez plus être contacté de cette façon, désactivez l'option dans \
            votre profil.

            Cordialement,
            L'équipe %s
            """
                .formatted(
                    recipientName,
                    senderName,
                    adName,
                    appName,
                    message,
                    senderName,
                    adUrl,
                    appName);
      }
      default -> throw new IllegalArgumentException("Unknown template: " + templateName);
    }
    Mail mail = Mail.withText(toEmail, subject, body);
    if (replyTo != null) {
      mail.setReplyTo(replyTo);
    }
    mailer.send(mail);
  }
}
