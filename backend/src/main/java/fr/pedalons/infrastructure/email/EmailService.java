package fr.pedalons.infrastructure.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.quarkus.qute.Engine;
import io.quarkus.qute.Template;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Renders {@code templates/mail/<name>.<lang>.{html,txt}} with Qute and hands the result to the
 * Quarkus mailer. Where that mail goes is the deployment's business alone: Scaleway Transactional
 * Email's SMTP relay in production, mailhog on a workstation, the mock mailbox in tests. The
 * templates in this repository are the only copy there is.
 */
@ApplicationScoped
public class EmailService {

  public static final String EMAIL_VERIFICATION = "email-verification";
  public static final String OTP = "otp";
  public static final String PASSWORD_RESET = "password-reset";
  public static final String DATA_EXPORT = "data-export";
  public static final String AD_CONTACT = "ad-contact";

  /**
   * The two invitation templates: "you already have an account, sign in" and "create one" differ
   * enough in wording that two files read better than one with a flag.
   */
  public static final String TEAM_INVITATION = "team-invitation";

  public static final String TEAM_INVITATION_SIGNUP = "team-invitation-signup";

  /**
   * The one template every notification type shares. Its parameters arrive already rendered
   * ({@code subject}, {@code title}, {@code body}, {@code ctaLabel}, {@code ctaUrl}…) by {@code
   * NotificationTexts}, so a new notification type needs no new template.
   */
  public static final String NOTIFICATION = "notification";

  /**
   * The daily digest: several notifications in one e-mail. {@code items} is a list of already
   * rendered {@code title}, {@code body}, {@code ctaLabel}, {@code ctaUrl}.
   */
  public static final String NOTIFICATION_DIGEST = "notification-digest";

  /** The languages {@code templates/mail} is translated into; anything else falls back to French. */
  private static final Set<String> TEMPLATE_LANGUAGES = Set.of("fr", "en");

  @Inject Mailer mailer;

  @Inject Engine engine;

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
   *
   * <p>The subject lives in a hidden fragment of the {@code .txt} file rather than the {@code
   * .html} one because Qute escapes everything in an HTML template, and these subjects interpolate
   * user-chosen names.
   */
  public void sendEmail(
      String toEmail,
      String templateName,
      String language,
      Map<String, Object> params,
      @Nullable String replyTo) {
    String lang = TEMPLATE_LANGUAGES.contains(language) ? language : "fr";
    String path = "mail/" + templateName + "." + lang;

    // The suffix is explicit: quarkus.qute.suffixes would otherwise resolve "mail/otp.fr" to the
    // .html file and the .txt sibling would be unreachable.
    Template textTemplate = engine.getTemplate(path + ".txt");
    Template htmlTemplate = engine.getTemplate(path + ".html");
    if (textTemplate == null || htmlTemplate == null) {
      throw new IllegalArgumentException("Unknown template: " + templateName + " / " + language);
    }

    // strip(): the newline that follows the hidden subject fragment is still part of the body.
    String subject = textTemplate.getFragment("subject").data("params", params).render().strip();
    String text = textTemplate.data("params", params).render().strip() + "\n";
    String html = htmlTemplate.data("params", params).render();

    // Both parts, so plain-text readers and spam filters each get something.
    Mail mail = Mail.withHtml(toEmail, subject, html).setText(text);
    if (replyTo != null) {
      mail.setReplyTo(replyTo);
    }
    mailer.send(mail);
  }
}
