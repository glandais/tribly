package fr.pedalons.service.timezone;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Naming a timezone for the « heure de Tokyo » mention of a rendezvous, in the texts the server
 * writes itself — notifications, e-mails, team webhooks (docs/LEDGER_*.md API-60, plan §7 « La
 * mention »).
 *
 * <p>The zone is named by the city of its IANA identifier — last segment, {@code _} → space — with
 * a small French table for the common cases; never the long name, the abbreviation or the offset.
 * This is the third copy of the rule, after {@code frontend/src/utils/zoneLabel.ts} and {@code
 * mobile/lib/core/utils/zone_label.dart}: keep the three tables in step.
 */
public final class ZoneLabels {

  /** Last IANA segment → French name, only where French differs from the identifier. */
  private static final Map<String, String> FRENCH_CITY_NAMES =
      Map.ofEntries(
          // Europe
          Map.entry("Athens", "Athènes"),
          Map.entry("Brussels", "Bruxelles"),
          Map.entry("Bucharest", "Bucarest"),
          Map.entry("Copenhagen", "Copenhague"),
          Map.entry("Lisbon", "Lisbonne"),
          Map.entry("London", "Londres"),
          Map.entry("Moscow", "Moscou"),
          Map.entry("Vienna", "Vienne"),
          Map.entry("Warsaw", "Varsovie"),
          // Africa
          Map.entry("Algiers", "Alger"),
          Map.entry("Cairo", "Le Caire"),
          // Americas
          Map.entry("Mexico_City", "Mexico"),
          Map.entry("Montreal", "Montréal"),
          Map.entry("Sao_Paulo", "São Paulo"),
          // Asia
          Map.entry("Singapore", "Singapour"),
          // Atlantic, Indian and Pacific islands
          Map.entry("Azores", "Açores"),
          Map.entry("Canary", "Canaries"),
          Map.entry("Noumea", "Nouméa"),
          Map.entry("Reunion", "La Réunion"));

  /**
   * The « of » phrase where elision alone gets it wrong: a plural article, or one the IANA segment
   * spells without it. {@code Le …} is contracted generically (« du Caire »).
   */
  private static final Map<String, String> FRENCH_CITY_OF =
      Map.of(
          "Azores", "des Açores",
          "Canary", "des Canaries",
          "Maldives", "des Maldives");

  /** Cities whose initial h is mute: « d'Helsinki ». Any other h is aspirated. */
  private static final Set<String> FRENCH_MUTE_H = Set.of("Helsinki", "Honolulu", "Ho Chi Minh");

  /** {@code Etc/GMT-9} is UTC+9: POSIX inverts the sign. Returned at sea, with no city to name. */
  private static final Pattern ETC_GMT = Pattern.compile("^Etc/GMT([+-])(\\d{1,2})$");

  private static final Set<String> UTC_ALIASES =
      Set.of("UTC", "Etc/UTC", "Etc/GMT", "Etc/UCT", "Etc/Zulu", "GMT");

  private static final Pattern FRENCH_VOWEL =
      Pattern.compile("^[AEIOUYÀÂÄÉÈÊËÎÏÔÖÙÛÜŸ]", Pattern.CASE_INSENSITIVE);

  private ZoneLabels() {}

  /**
   * The city naming {@code timezone}, in {@code language} ({@code fr…} uses the French table,
   * anything else the IANA spelling): {@code Asia/Tokyo} → « Tokyo », {@code America/New_York} →
   * « New York », {@code Europe/London} → « Londres » in French, {@code Etc/GMT-9} → « UTC+9 ».
   */
  public static String cityName(String timezone, String language) {
    if (UTC_ALIASES.contains(timezone)) {
      return "UTC";
    }
    Matcher etc = ETC_GMT.matcher(timezone);
    if (etc.matches()) {
      int hours = Integer.parseInt(etc.group(2));
      if (hours == 0) {
        return "UTC";
      }
      return "UTC" + ("-".equals(etc.group(1)) ? "+" : "-") + hours;
    }
    String segment = segment(timezone);
    String french = isFrench(language) ? FRENCH_CITY_NAMES.get(segment) : null;
    return french != null ? french : segment.replace('_', ' ');
  }

  /**
   * The « of » phrase naming {@code timezone}, for « heure {ofCity} »: in French « de Tokyo »,
   * « d'Athènes », « du Caire », « des Açores », « de La Réunion », and bare « UTC+9 »; in any other
   * language the city name itself.
   */
  public static String cityOf(String timezone, String language) {
    String city = cityName(timezone, language);
    if (!isFrench(language) || city.startsWith("UTC")) {
      return city;
    }
    String fixed = FRENCH_CITY_OF.get(segment(timezone));
    if (fixed != null) {
      return fixed;
    }
    if (city.startsWith("Le ")) {
      return "du " + city.substring(3);
    }
    if (city.startsWith("Les ")) {
      return "des " + city.substring(4);
    }
    if (FRENCH_VOWEL.matcher(city).find() || FRENCH_MUTE_H.contains(city)) {
      return "d'" + city;
    }
    return "de " + city;
  }

  /**
   * Whether {@code a} and {@code b} have the same UTC offset at {@code instant} — the test for
   * showing the mention: offsets, not identifiers, so Paris read from Brussels says nothing.
   */
  public static boolean sameOffsetAt(Instant instant, ZoneId a, ZoneId b) {
    return a.equals(b) || a.getRules().getOffset(instant).equals(b.getRules().getOffset(instant));
  }

  private static String segment(String timezone) {
    return timezone.substring(timezone.lastIndexOf('/') + 1);
  }

  private static boolean isFrench(String language) {
    return language.toLowerCase(Locale.ROOT).startsWith("fr");
  }
}
