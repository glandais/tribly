package fr.pedalons.service.calendar;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.dto.calendar.response.CalendarEventDto;
import fr.pedalons.dto.calendar.response.CalendarEventType;
import fr.pedalons.enums.Status;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class IcsGenerationServiceTest extends AbstractBaseTest {

  @Inject IcsGenerationService icsGenerationService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
  }

  @Test
  void generateIcs_shouldReturnValidIcsFormat() {
    CalendarEventDto event =
        new CalendarEventDto(
            "abc123",
            "Morning Ride",
            Instant.parse("2024-06-15T08:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "cycling-team",
            "Cycling Team",
            "morning-ride",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "My Calendar");

    assertTrue(ics.startsWith("BEGIN:VCALENDAR\r\n"));
    assertTrue(ics.endsWith("END:VCALENDAR\r\n"));
    assertTrue(ics.contains("VERSION:2.0"));
    assertTrue(ics.contains("PRODID:-//Pedalons//Calendar//EN"));
    assertTrue(ics.contains("X-WR-CALNAME:My Calendar"));
  }

  @Test
  void generateIcs_shouldIncludeEventDetails() {
    CalendarEventDto event =
        new CalendarEventDto(
            "event123",
            "Test Ride",
            Instant.parse("2024-06-15T08:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "team-slug",
            "Team Name",
            "test-ride",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar");

    assertTrue(ics.contains("BEGIN:VEVENT"));
    assertTrue(ics.contains("END:VEVENT"));
    assertTrue(ics.contains("UID:event123@pedalons.app"));
    assertTrue(ics.contains("SUMMARY:Test Ride"));
    assertTrue(ics.contains("DESCRIPTION:Team Name"));
    assertTrue(ics.contains("CATEGORIES:RIDE"));
  }

  @Test
  void generateIcs_shouldHandleAllDayEvents() {
    CalendarEventDto event =
        new CalendarEventDto(
            "allday123",
            "All Day Event",
            Instant.parse("2024-06-15T00:00:00Z"),
            Instant.parse("2024-06-16T00:00:00Z"),
            true,
            CalendarEventType.TRIP_STAGE,
            "team-slug",
            "Team Name",
            "stage-1",
            "trip-slug",
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar");

    assertTrue(ics.contains("DTSTART;VALUE=DATE:20240615"));
    assertTrue(ics.contains("DTEND;VALUE=DATE:20240616"));
    assertTrue(ics.contains("CATEGORIES:TRIP"));
  }

  @Test
  void generateIcs_shouldHandleTimedEvents() {
    CalendarEventDto event =
        new CalendarEventDto(
            "timed123",
            "Timed Ride",
            Instant.parse("2024-06-15T08:30:00Z"),
            Instant.parse("2024-06-15T12:30:00Z"),
            false,
            CalendarEventType.RIDE,
            "team-slug",
            "Team Name",
            "timed-ride",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar");

    assertTrue(ics.contains("DTSTART:20240615T083000Z"));
    assertTrue(ics.contains("DTEND:20240615T123000Z"));
  }

  @Test
  void generateIcs_shouldIncludeRideUrl() {
    CalendarEventDto event =
        new CalendarEventDto(
            "ride123",
            "Ride Event",
            Instant.parse("2024-06-15T08:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "cycling-club",
            "Cycling Club",
            "sunday-ride",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar");

    assertTrue(ics.contains("URL:"));
    assertTrue(ics.contains("/teams/cycling-club/rides/sunday-ride"));
  }

  @Test
  void generateIcs_shouldIncludeTripStageUrl() {
    CalendarEventDto event =
        new CalendarEventDto(
            "stage123",
            "Stage Event",
            Instant.parse("2024-06-15T00:00:00Z"),
            null,
            true,
            CalendarEventType.TRIP_STAGE,
            "cycling-club",
            "Cycling Club",
            "stage-1",
            "summer-trip",
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar");

    assertTrue(ics.contains("URL:"));
    assertTrue(ics.contains("/teams/cycling-club/trips/summer-trip/stages/stage-1"));
  }

  @Test
  void generateIcs_shouldEscapeSpecialCharacters() {
    CalendarEventDto event =
        new CalendarEventDto(
            "special123",
            "Ride with, commas; and semicolons",
            Instant.parse("2024-06-15T08:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "team",
            "Team, Name; Test",
            "ride",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event), "Calendar; Test, Name");

    assertTrue(ics.contains("SUMMARY:Ride with\\, commas\\; and semicolons"));
    assertTrue(ics.contains("DESCRIPTION:Team\\, Name\\; Test"));
    assertTrue(ics.contains("X-WR-CALNAME:Calendar\\; Test\\, Name"));
  }

  @Test
  void generateIcs_shouldHandleMultipleEvents() {
    CalendarEventDto event1 =
        new CalendarEventDto(
            "event1",
            "First Event",
            Instant.parse("2024-06-15T08:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "team",
            "Team",
            "first",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    CalendarEventDto event2 =
        new CalendarEventDto(
            "event2",
            "Second Event",
            Instant.parse("2024-06-16T09:00:00Z"),
            null,
            false,
            CalendarEventType.RIDE,
            "team",
            "Team",
            "second",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            false,
            null,
            Status.PUBLISHED);

    String ics = icsGenerationService.generateIcs(List.of(event1, event2), "Calendar");

    // Count VEVENT blocks
    int eventCount = ics.split("BEGIN:VEVENT").length - 1;
    assertEquals(2, eventCount);

    assertTrue(ics.contains("UID:event1@pedalons.app"));
    assertTrue(ics.contains("UID:event2@pedalons.app"));
  }

  @Test
  void generateIcs_shouldHandleEmptyEventList() {
    String ics = icsGenerationService.generateIcs(List.of(), "Empty Calendar");

    assertTrue(ics.contains("BEGIN:VCALENDAR"));
    assertTrue(ics.contains("END:VCALENDAR"));
    assertTrue(ics.contains("X-WR-CALNAME:Empty Calendar"));
    assertFalse(ics.contains("BEGIN:VEVENT"));
  }

  @Test
  void generateIcs_shouldIncludeRefreshInterval() {
    String ics =
        icsGenerationService.generateIcs(
            List.of(
                new CalendarEventDto(
                    "id",
                    "Event",
                    Instant.now(),
                    null,
                    false,
                    CalendarEventType.RIDE,
                    "team",
                    "Team",
                    "event",
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    false,
                    null,
                    Status.PUBLISHED)),
            "Calendar");

    assertTrue(ics.contains("REFRESH-INTERVAL;VALUE=DURATION:PT1H"));
  }
}
