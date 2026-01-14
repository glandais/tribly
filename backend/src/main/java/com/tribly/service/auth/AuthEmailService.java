package com.tribly.service.auth;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class AuthEmailService {

  @Inject Mailer mailer;

  @ConfigProperty(name = "tribly.base-url", defaultValue = "http://localhost:5173")
  String baseUrl;

  @ConfigProperty(name = "tribly.app-name", defaultValue = "Tribly")
  String appName;

  public void sendVerificationEmail(String email, String displayName, String token) {
    String verifyUrl = baseUrl + "/verify-email?token=" + token;
    String subject = "Confirmez votre adresse email - " + appName;
    String body =
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

    mailer.send(Mail.withText(email, subject, body));
  }

  public void sendMagicLinkEmail(String email, String token) {
    String loginUrl = baseUrl + "/magic-link/verify?token=" + token;
    String subject = "Connexion à " + appName;
    String body =
        """
        Bonjour,

        Cliquez sur le lien ci-dessous pour vous connecter à %s :

        %s

        Ce lien expirera dans 15 minutes et ne peut être utilisé qu'une seule fois.

        Si vous n'avez pas demandé ce lien, vous pouvez ignorer cet email.

        Cordialement,
        L'équipe %s
        """
            .formatted(appName, loginUrl, appName);

    mailer.send(Mail.withText(email, subject, body));
  }
}
