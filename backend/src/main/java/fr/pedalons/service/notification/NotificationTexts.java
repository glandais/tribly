package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationType;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Server-side wording of the notifications, for the channels that need finished text (e-mail today,
 * push tomorrow). The inbox does not use it: clients localize from the type and structured fields.
 *
 * <p>A properties file per language rather than one Brevo template per type: the e-mail goes
 * through a single generic template whose parameters are already rendered here, so adding a type
 * means adding four lines to two files, not two templates to Brevo.
 */
@ApplicationScoped
public class NotificationTexts {

  static final String DEFAULT_LANGUAGE = "fr";
  static final ZoneId DEFAULT_ZONE = ZoneId.of("Europe/Paris");
  static final String[] KEYS = {"subject", "title", "body", "cta"};

  private static final Map<String, DateTimeFormatter> DATE_FORMATS =
      Map.of(
          "fr", DateTimeFormatter.ofPattern("EEEE d MMMM 'à' HH'h'mm", Locale.FRENCH),
          "en", DateTimeFormatter.ofPattern("EEEE, MMMM d 'at' h:mm a", Locale.ENGLISH));

  private static final Pattern PLACEHOLDER = Pattern.compile("\\{(\\w+)}");

  private final Map<String, Properties> bundles = new ConcurrentHashMap<>();

  public record Rendered(String subject, String title, String body, String cta) {}

  public Rendered render(NotificationMessage message) {
    String language = language(message.recipientLanguage());
    Properties texts = bundle(language);
    Map<String, String> values =
        Map.of(
            "actor", message.actorName() != null ? message.actorName() : message.teamName(),
            "team", message.teamName(),
            "subject", message.subjectName(),
            "date", formatDate(message, language),
            "excerpt", message.excerpt() != null ? message.excerpt() : "",
            "site", message.siteName());
    String prefix = message.type().name() + ".";
    return new Rendered(
        fill(texts, prefix + "subject", values),
        fill(texts, prefix + "title", values),
        fill(texts, prefix + "body", values),
        fill(texts, prefix + "cta", values));
  }

  /** "fr-CA" reads the French file; a language nobody translated reads the default. */
  static String language(@Nullable String tag) {
    if (tag == null) {
      return DEFAULT_LANGUAGE;
    }
    String primary = tag.split("-")[0].toLowerCase(Locale.ROOT);
    return DATE_FORMATS.containsKey(primary) ? primary : DEFAULT_LANGUAGE;
  }

  private static String formatDate(NotificationMessage message, String language) {
    if (message.subjectDateTime() == null) {
      return "";
    }
    return DATE_FORMATS
        .get(language)
        .withZone(zone(message.recipientTimezone()))
        .format(message.subjectDateTime());
  }

  private static ZoneId zone(@Nullable String timezone) {
    if (timezone == null) {
      return DEFAULT_ZONE;
    }
    try {
      return ZoneId.of(timezone);
    } catch (DateTimeException e) {
      return DEFAULT_ZONE;
    }
  }

  private static String fill(Properties texts, String key, Map<String, String> values) {
    String template = texts.getProperty(key);
    if (template == null) {
      throw new IllegalStateException("Missing notification text " + key);
    }
    // One pass, so a ride named "{team}" stays a ride named "{team}".
    return PLACEHOLDER
        .matcher(template)
        .replaceAll(m -> Matcher.quoteReplacement(values.getOrDefault(m.group(1), m.group())));
  }

  Properties bundle(String language) {
    return bundles.computeIfAbsent(language, NotificationTexts::load);
  }

  private static Properties load(String language) {
    String path = "notifications/texts_" + language + ".properties";
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {
      if (in == null) {
        throw new IllegalStateException("Missing " + path);
      }
      Properties properties = new Properties();
      properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
      return properties;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /** Every language with a texts file — what {@code NotificationTextsTest} checks for completeness. */
  static Iterable<String> languages() {
    return DATE_FORMATS.keySet();
  }

  static boolean isComplete(Properties texts, NotificationType type) {
    for (String key : KEYS) {
      if (texts.getProperty(type.name() + "." + key) == null) {
        return false;
      }
    }
    return true;
  }
}
