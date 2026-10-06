package fr.pedalons.service.calendar;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.calendar.response.CalendarEventDto;
import fr.pedalons.dto.calendar.response.CalendarEventType;
import fr.pedalons.dto.publications.response.TeamPublicationDto;
import fr.pedalons.dto.rides.response.RideDto;
import fr.pedalons.dto.trips.response.TripDto;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.trip.TripStageRepository;
import fr.pedalons.service.publication.PublicationEndCalculator;
import fr.pedalons.service.ride.RideService;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.trip.TripService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

/**
 * One ride or one trip as a downloadable calendar — « ajouter au calendrier » on a card
 * (docs/LEDGER_DONE.md WEB-33).
 *
 * <p>Unlike the subscription feeds of {@link CalendarService}, this takes no token: the file is
 * built from the same DTO as the detail page, so whoever may read the ride or the trip — an
 * anonymous visitor on a public one included — gets its calendar, and nobody else does. The
 * {@code @CheckAccess(READ)} here is the same check {@code getDto} makes: it is the one the
 * architecture test requires on every service a resource calls.
 */
@ApplicationScoped
public class PublicationIcsService {

  @Inject RideService rideService;
  @Inject TripService tripService;
  @Inject IcsGenerationService icsGenerationService;
  @Inject PublicationEndCalculator publicationEndCalculator;
  @Inject StageTimezones stageTimezones;
  @Inject TripStageRepository tripStageRepository;

  @CheckAccess(entityType = EntityType.RIDE, action = ActionType.READ)
  public String rideIcs(String teamSlug, String rideSlug) {
    RideDto ride = rideService.getDto(teamSlug, rideSlug);
    CalendarEventDto event =
        event(
            ride.getTeam(),
            ride.getId(),
            ride.getName(),
            ride.getDateTime(),
            ride.getEndDateTime(),
            false,
            CalendarEventType.RIDE,
            ride.getSlug(),
            null,
            ride.getStatus());
    return icsGenerationService.generateIcs(List.of(event), ride.getName());
  }

  /** A trip is its stages: one all-day event each, as in the subscription feeds. */
  @CheckAccess(entityType = EntityType.TRIP, action = ActionType.READ)
  public String tripIcs(String teamSlug, String tripSlug) {
    TripDto trip = tripService.getDto(teamSlug, tripSlug);
    List<Long> stageIds =
        trip.getStages().stream().map(stage -> TsidUtils.toLong(stage.id())).toList();
    // Each stage up to its own end — one query for all of them (docs/LEDGER_*.md API-85).
    Map<Long, Instant> ends = publicationEndCalculator.effectiveEnds(stageIds);
    // And its days counted where it starts (docs/LEDGER_*.md API-90).
    Map<String, ZoneId> zones = new HashMap<>();
    stageTimezones
        .of(tripStageRepository.list("id in ?1", stageIds))
        .forEach((id, zone) -> zones.put(TsidUtils.toString(id), zone));
    List<CalendarEventDto> events =
        trip.getStages().stream()
            .map(
                stage ->
                    event(
                        trip.getTeam(),
                        stage.id(),
                        stage.name(),
                        stage.dateTime(),
                        ends.get(TsidUtils.toLong(stage.id())),
                        true,
                        CalendarEventType.TRIP_STAGE,
                        stage.slug(),
                        trip.getSlug(),
                        trip.getStatus()))
            .toList();
    return icsGenerationService.generateIcs(events, trip.getName(), zones);
  }

  /** Only what the ICS writer reads is set; the card-only fields stay empty. */
  private static CalendarEventDto event(
      TeamPublicationDto team,
      String id,
      String title,
      Instant start,
      @Nullable Instant end,
      boolean allDay,
      CalendarEventType type,
      String entitySlug,
      @Nullable String tripSlug,
      Status status) {
    return new CalendarEventDto(
        id,
        title,
        start,
        end,
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
