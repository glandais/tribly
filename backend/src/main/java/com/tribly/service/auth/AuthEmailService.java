package com.tribly.service.auth;

import com.tribly.domain.platform.Domain;
import com.tribly.service.security.DomainResolver;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AuthEmailService {

  @Inject Mailer mailer;

  @Inject DomainResolver domainResolver;

  public void sendVerificationEmail(String email, String displayName, String token) {
    Domain domain = domainResolver.getDomain();
    String appName = domain.getName();
    String verifyUrl = domain.getBaseUrl() + "/verify-email?token=" + token;
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
    Domain domain = domainResolver.getDomain();
    String appName = domain.getName();
    String loginUrl = domain.getBaseUrl() + "/magic-link/verify?token=" + token;
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
