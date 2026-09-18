package fr.pedalons.service.notification;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.enums.NotificationChannel;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.NotificationType;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/** Plain unit test: no Quarkus, the texts are classpath files. */
class NotificationTextsTest {

  private final NotificationTexts texts = new NotificationTexts();

  private static NotificationMessage message(
      NotificationType type, @Nullable String language, String subjectName) {
    return new NotificationMessage(
        1L,
        NotificationChannel.EMAIL,
        type,
        "rider@example.com",
        "Rider",
        language,
        "Europe/Paris",
        "Alice",
        "club",
        "Le Club",
        NotificationSubjectType.RIDE,
        "sortie",
        subjectName,
        Instant.parse("2026-09-20T06:30:00Z"),
        "8h30 au parking",
        "https://club.example",
        "Le Club");
  }

  @Test
  void everyTypeIsWordedInEveryLanguage() {
    for (String language : NotificationTexts.languages()) {
      for (NotificationType type : NotificationType.values()) {
        assertTrue(
            NotificationTexts.isComplete(texts.bundle(language), type),
            type + " is missing a key in texts_" + language + ".properties");
      }
    }
  }

  @Test
  void rendersInTheRecipientsLanguageAndTimezone() {
    NotificationTexts.Rendered fr =
        texts.render(message(NotificationType.RIDE_PUBLISHED, "fr-FR", "Col du Galibier"));
    assertEquals("Nouvelle sortie : Col du Galibier", fr.subject());
    assertEquals(
        "Alice a publié la sortie « Col du Galibier », prévue le dimanche 20 septembre à 08h30.",
        fr.body());

    NotificationTexts.Rendered en =
        texts.render(message(NotificationType.RIDE_PUBLISHED, "en", "Col du Galibier"));
    assertEquals("New ride: Col du Galibier", en.subject());
  }

  @Test
  void unknownLanguage_fallsBackToFrench() {
    assertEquals(
        "Nouvelle sortie dans Le Club",
        texts.render(message(NotificationType.RIDE_PUBLISHED, "de", "x")).title());
  }

  @Test
  void placeholdersInUserContent_areNotExpanded() {
    assertEquals(
        "Nouvelle sortie : {team}",
        texts.render(message(NotificationType.RIDE_PUBLISHED, null, "{team}")).subject());
  }

  @Test
  void links_pointAtTheCanonicalWebRoute() {
    assertEquals(
        "https://club.example/teams/club/rides/sortie",
        message(NotificationType.RIDE_PUBLISHED, null, "x").subjectUrl());
  }

  @Test
  void excerpt_isFlattenedAndCut() {
    String excerpt = NotificationRecipientResolver.excerpt("  a\n\n b " + "c".repeat(400));
    assertTrue(excerpt.startsWith("a b c"));
    assertEquals(NotificationRecipientResolver.EXCERPT_LENGTH, excerpt.length());
    assertTrue(excerpt.endsWith("…"));
  }
}
