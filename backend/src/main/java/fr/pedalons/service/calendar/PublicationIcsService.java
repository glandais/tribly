package fr.pedalons.service.calendar;

import fr.pedalons.dto.calendar.response.CalendarEventDto;
import fr.pedalons.dto.calendar.response.CalendarEventType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.rides.response.RideDto;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.enums.Status;
import fr.pedalons.service.ride.RideService;
import fr.pedalons.service.trip.TripService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

/**
 * One ride or one trip as a downloadable calendar — « ajouter au calendrier » on a card
 * (docs/LEDGER_DONE.md WEB-33).
 *
 * <p>Unlike the subscription feeds of {@link CalendarService}, this takes no token: the file is
 * built from the same DTO as the detail page, so whoever may read the ride or the trip — an
 * anonymous visitor on a public one included — gets its calendar, and nobody else does.
 */
@ApplicationScoped
public class PublicationIcsService {

  @Inject RideService rideService;
  @Inject TripService tripService;
  @Inject IcsGenerationService icsGenerationService;

  public String rideIcs(String teamSlug, String rideSlug) {
    RideDto ride = rideService.getDto(teamSlug, rideSlug);
    CalendarEventDto event =
        event(
            ride.getTeam(),
            ride.getId(),
            ride.getName(),
            ride.getDateTime(),
            false,
            CalendarEventType.RIDE,
            ride.getSlug(),
            null,
            ride.getStatus());
    return icsGenerationService.generateIcs(List.of(event), ride.getName());
  }

  /** A trip is its stages: one all-day event each, as in the subscription feeds. */
  public String tripIcs(String teamSlug, String tripSlug) {
    TripDto trip = tripService.getDto(teamSlug, tripSlug);
    List<CalendarEventDto> events =
        trip.getStages().stream()
            .map(
                stage ->
                    event(
                        trip.getTeam(),
                        stage.id(),
                        stage.name(),
                        stage.dateTime(),
                        true,
                        CalendarEventType.TRIP_STAGE,
                        stage.slug(),
                        trip.getSlug(),
                        trip.getStatus()))
            .toList();
    return icsGenerationService.generateIcs(events, trip.getName());
  }

  /** Only what the ICS writer reads is set; the card-only fields stay empty. */
  private static CalendarEventDto event(
      TeamPublicationDto team,
      String id,
      String title,
      Instant start,
      boolean allDay,
      CalendarEventType type,
      String entitySlug,
      @Nullable String tripSlug,
      Status status) {
    return new CalendarEventDto(
        id,
        title,
        start,
        null,
        allDay,
        type,
        team.slug(),
        team.name(),
        entitySlug,
        tripSlug,
        null,
        null,
        null,
        null,
        null,
        null,
        false,
        null,
        status);
  }
}
