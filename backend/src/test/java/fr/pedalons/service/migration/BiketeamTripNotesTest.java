package fr.pedalons.service.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.service.migration.BiketeamModel.BtTrip;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/** {@link BiketeamMigrationService#renderTripDescription}: a trip's notes page joins its text. */
class BiketeamTripNotesTest {

  private static BtTrip trip(@Nullable String description, @Nullable String notes) {
    return new BtTrip(
        "trip-1",
        "team",
        null,
        null,
        null,
        null,
        null,
        "PUBLISHED",
        null,
        "Tour",
        description,
        null,
        null,
        notes,
        true,
        false);
  }

  @Test
  void noNotes_leavesTheDescriptionAsBefore() {
    assertEquals(
        "Deux  \njours", BiketeamMigrationService.renderTripDescription(trip("Deux\njours", null)));
    assertEquals("Deux", BiketeamMigrationService.renderTripDescription(trip("Deux", "  \r\n ")));
  }

  @Test
  void notes_followTheDescription_underANotesHeading_withLfLineEndings() {
    assertEquals(
        "Deux jours\n\n## Notes\n\nPrévoir un **gilet**.\nGîte réservé.",
        BiketeamMigrationService.renderTripDescription(
            trip("Deux jours\r\n", "\r\nPrévoir un **gilet**.\r\nGîte réservé.\r\n")));
  }

  @Test
  void notesAlone_makeTheWholeDescription() {
    assertEquals(
        "## Notes\n\nGîte réservé.",
        BiketeamMigrationService.renderTripDescription(trip(null, "Gîte réservé.")));
  }
}
