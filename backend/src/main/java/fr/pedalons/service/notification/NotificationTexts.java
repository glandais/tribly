package fr.pedalons.service.notification;

import fr.pedalons.enums.NotificationChange;
import fr.pedalons.enums.NotificationType;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Server-side wording of the notifications, for the channels that need finished text: e-mail, push,
 * the team webhook. The inbox does not use it: clients localize from the type and structured fields.
 *
 * <p>A properties file per language rather than one mail template per type: the e-mail goes
 * through a single generic template whose parameters are already rendered here, so adding a type
 * means adding four lines to two files, not two more Qute templates.
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
    return render(
        message.type(),
        message.recipientLanguage(),
        message.recipientTimezone(),
        message.actorName(),
        message.teamName(),
        message.subjectName(),
        message.subjectDateTime(),
        message.excerpt(),
        message.siteName(),
        message.changes());
  }

  /**
   * The same wording for a reader who is not a member — a team webhook, which has a language of its
   * own and no time zone but the default.
   */
  public Rendered render(
      NotificationType type,
      @Nullable String languageTag,
      @Nullable String timezone,
      @Nullable String actorName,
      String teamName,
      String subjectName,
      @Nullable Instant subjectDateTime,
      @Nullable String excerpt,
      String siteName,
      List<NotificationChange> changes) {
    String language = language(languageTag);
    Properties texts = bundle(language);
    String date =
        subjectDateTime == null
            ? ""
            : DATE_FORMATS.get(language).withZone(zone(timezone)).format(subjectDateTime);
    Map<String, String> values = new HashMap<>();
    values.put("actor", actorName != null ? actorName : teamName);
    values.put("team", teamName);
    values.put("subject", subjectName);
    values.put("date", date);
    values.put("excerpt", excerpt != null ? excerpt : "");
    values.put("site", siteName);
    // Each change is a clause of its own, filled with the same values, then the clauses joined.
    values.put(
        "changes",
        String.join(
            texts.getProperty("change.separator", "; "),
            changes.stream().map(c -> fill(texts, "change." + c.name(), values)).toList()));
    String prefix = type.name() + ".";
    return new Rendered(
        fill(texts, prefix + "subject", values),
        fill(texts, prefix + "title", values),
        fill(texts, prefix + "body", values),
        fill(texts, prefix + "cta", values));
  }

  /** The test message of a team webhook: its {@code title} or {@code body}. */
  public String webhookTestText(String languageTag, String key, String teamName, String siteName) {
    return fill(
        bundle(language(languageTag)),
        "webhook.test." + key,
        Map.of("team", teamName, "site", siteName));
  }

  /** A line of the daily digest's frame: its subject and its heading. */
  public String digestText(@Nullable String languageTag, String key, int count) {
    return fill(
        bundle(language(languageTag)), "digest." + key, Map.of("count", String.valueOf(count)));
  }

  /** "fr-CA" reads the French file; a language nobody translated reads the default. */
  static String language(@Nullable String tag) {
    if (tag == null) {
      return DEFAULT_LANGUAGE;
    }
    String primary = tag.split("-")[0].toLowerCase(Locale.ROOT);
    return DATE_FORMATS.containsKey(primary) ? primary : DEFAULT_LANGUAGE;
  }

  static ZoneId zone(@Nullable String timezone) {
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

  /** The keys outside the per-type blocks: change clauses, digest frame, webhook test. */
  static boolean isFrameComplete(Properties texts) {
    for (NotificationChange change : NotificationChange.values()) {
      if (texts.getProperty("change." + change.name()) == null) {
        return false;
      }
    }
    for (String key :
        new String[] {
          "digest.subject",
          "digest.title",
          "digest.intro",
          "webhook.test.title",
          "webhook.test.body"
        }) {
      if (texts.getProperty(key) == null) {
        return false;
      }
    }
    return true;
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
