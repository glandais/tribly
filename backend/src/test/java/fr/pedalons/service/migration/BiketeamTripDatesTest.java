package fr.pedalons.service.migration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import fr.pedalons.service.migration.BiketeamModel.BtTrip;
import fr.pedalons.service.migration.BiketeamModel.BtTripStage;
import java.time.LocalDate;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * {@link BiketeamMigrationService#stagesOutsideDates}: a trip whose stages leave its biketeam dates
 * is reported, not corrected.
 */
class BiketeamTripDatesTest {

  private static BtTrip trip(@Nullable String start, @Nullable String end) {
    return new BtTrip(
        "trip-1",
        "team",
        null,
        start != null ? LocalDate.parse(start) : null,
        end != null ? LocalDate.parse(end) : null,
        null,
        null,
        "PUBLISHED",
        null,
        "Tour",
        null,
        null,
        null,
        null,
        true,
        false);
  }

  private static BtTripStage stage(String name, @Nullable String date) {
    return new BtTripStage(
        "s-" + name, "trip-1", date != null ? LocalDate.parse(date) : null, name, null, false);
  }

  @Test
  void stagesWithinTheDates_boundsIncluded_areFine() {
    assertNull(
        BiketeamMigrationService.stagesOutsideDates(
            trip("2025-07-01", "2025-07-03"),
            List.of(stage("A", "2025-07-01"), stage("B", "2025-07-02"), stage("C", "2025-07-03"))));
  }

  @Test
  void aMissingDate_isNoBound() {
    assertNull(
        BiketeamMigrationService.stagesOutsideDates(
            trip(null, null), List.of(stage("A", "2025-07-01"), stage("B", null))));
    assertNull(
        BiketeamMigrationService.stagesOutsideDates(
            trip("2025-07-01", null), List.of(stage("A", "2030-01-01"))));
  }

  @Test
  void aStageAfterTheEnd_isReported_withTheDates() {
    assertEquals(
        "Trip 'Tour' runs from 2025-07-01 to 2025-07-02 on biketeam, but stage 'B' (2025-07-05)"
            + " falls outside those dates. Pédalons ends a trip with its last stage: the biketeam"
            + " end date is lost, and the trip may end before it starts. Fix the dates on"
            + " biketeam, or on Pédalons after the migration.",
        BiketeamMigrationService.stagesOutsideDates(
            trip("2025-07-01", "2025-07-02"),
            List.of(stage("A", "2025-07-01"), stage("B", "2025-07-05"))));
  }

  @Test
  void everyStageOutside_isNamed_beforeTheStartToo() {
    String message =
        BiketeamMigrationService.stagesOutsideDates(
            trip("2025-07-01", "2025-07-02"),
            List.of(stage("A", "2025-06-30"), stage("B", "2025-07-01"), stage("C", "2025-07-09")));
    assertEquals(
        "Trip 'Tour' runs from 2025-07-01 to 2025-07-02 on biketeam, but stages 'A' (2025-06-30),"
            + " 'C' (2025-07-09) fall outside those dates. Pédalons ends a trip with its last"
            + " stage: the biketeam end date is lost, and the trip may end before it starts. Fix"
            + " the dates on biketeam, or on Pédalons after the migration.",
        message);
  }
}
