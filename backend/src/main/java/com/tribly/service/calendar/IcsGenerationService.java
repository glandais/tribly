package com.tribly.service.calendar;

import com.tribly.dto.calendar.response.CalendarEventDto;
import com.tribly.service.security.DomainResolver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class IcsGenerationService {

  private static final DateTimeFormatter ICS_DATETIME_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneId.of("UTC"));

  private static final DateTimeFormatter ICS_DATE_FORMAT =
      DateTimeFormatter.ofPattern("yyyyMMdd").withZone(ZoneId.of("UTC"));

  @Inject DomainResolver domainResolver;

  public String generateIcs(List<CalendarEventDto> events, String calendarName) {
    StringBuilder ics = new StringBuilder();

    // VCALENDAR header with Apple/Google compatibility
    ics.append("BEGIN:VCALENDAR\r\n");
    ics.append("VERSION:2.0\r\n");
    ics.append("PRODID:-//Tribly//Calendar//EN\r\n");
    ics.append("CALSCALE:GREGORIAN\r\n");
    ics.append("METHOD:PUBLISH\r\n");
    ics.append("X-WR-CALNAME:").append(escapeIcs(calendarName)).append("\r\n");
    ics.append("X-WR-TIMEZONE:Europe/Paris\r\n");
    ics.append("REFRESH-INTERVAL;VALUE=DURATION:PT1H\r\n");

    // Add each event
    for (CalendarEventDto event : events) {
      appendEvent(ics, event);
    }

    ics.append("END:VCALENDAR\r\n");
    return ics.toString();
  }

  private void appendEvent(StringBuilder ics, CalendarEventDto event) {
    ics.append("BEGIN:VEVENT\r\n");

    // UID must be globally unique and stable
    ics.append("UID:").append(event.id()).append("@tribly.app\r\n");

    // DTSTAMP is required - when the event was created/modified
    ics.append("DTSTAMP:").append(ICS_DATETIME_FORMAT.format(Instant.now())).append("\r\n");

    // Start date/time
    if (event.allDay()) {
      ics.append("DTSTART;VALUE=DATE:")
          .append(ICS_DATE_FORMAT.format(event.start()))
          .append("\r\n");
    } else {
      ics.append("DTSTART:").append(ICS_DATETIME_FORMAT.format(event.start())).append("\r\n");
    }

    // End date/time (optional)
    if (event.end() != null) {
      if (event.allDay()) {
        ics.append("DTEND;VALUE=DATE:").append(ICS_DATE_FORMAT.format(event.end())).append("\r\n");
      } else {
        ics.append("DTEND:").append(ICS_DATETIME_FORMAT.format(event.end())).append("\r\n");
      }
    }

    // Summary (title)
    ics.append("SUMMARY:").append(escapeIcs(event.title())).append("\r\n");

    // Description with team name
    ics.append("DESCRIPTION:").append(escapeIcs(event.teamName())).append("\r\n");

    // URL to the event
    String url = buildEventUrl(event);
    ics.append("URL:").append(url).append("\r\n");

    // Categories
    switch (event.type()) {
      case RIDE -> ics.append("CATEGORIES:RIDE\r\n");
      case TRIP_STAGE -> ics.append("CATEGORIES:TRIP\r\n");
    }

    ics.append("END:VEVENT\r\n");
  }

  private String buildEventUrl(CalendarEventDto event) {
    String baseUrl = domainResolver.getDomain().getBaseUrl();
    return switch (event.type()) {
      case RIDE -> baseUrl + "/teams/" + event.teamSlug() + "/rides/" + event.entitySlug();
      case TRIP_STAGE -> {
        if (event.tripSlug() != null) {
          yield baseUrl
              + "/teams/"
              + event.teamSlug()
              + "/trips/"
              + event.tripSlug()
              + "/stages/"
              + event.entitySlug();
        }
        yield baseUrl;
      }
    };
  }

  /**
   * Escape special characters in iCalendar text values per RFC 5545. Backslash, semicolon, and
   * comma must be escaped. Newlines must be escaped as \n.
   */
  private String escapeIcs(String text) {
    if (text == null) {
      return "";
    }
    return text.replace("\\", "\\\\")
        .replace(";", "\\;")
        .replace(",", "\\,")
        .replace("\r\n", "\\n")
        .replace("\n", "\\n")
        .replace("\r", "\\n");
  }
}
