package fr.pedalons.service.timezone;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.infrastructure.timezone.TimezoneService;
import fr.pedalons.service.timezone.EventTimezoneResolver.StagePoints;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * The chains of plan §4 (docs/LEDGER_*.md API-60): each link of each chain, a stage that inherits
 * its zone from the previous one, the team as the last resort — never UTC.
 */
class EventTimezoneResolverTest {

  private static final ZoneId PARIS = ZoneId.of("Europe/Paris");
  private static final ZoneId TOKYO = ZoneId.of("Asia/Tokyo");
  private static final ZoneId NEW_YORK = ZoneId.of("America/New_York");
  private static final ZoneId MONTREAL = ZoneId.of("America/Montreal");
  private static final ZoneId LONDON = ZoneId.of("Europe/London");

  private static EventTimezoneResolver resolver;

  @BeforeAll
  static void setUp() {
    resolver = new EventTimezoneResolver();
    resolver.timezoneService = new TimezoneService();
  }

  // ─── Points ───────────────────────────────────────────────────────────────

  @Test
  void resolve_aPointGivesItsZone() {
    assertEquals(TOKYO, resolver.resolve(team(MONTREAL), point(WGS84, g(139.76, 35.68))));
  }

  @Test
  void resolve_withoutPoint_isTheTeamsZone_notUtc() {
    assertEquals(MONTREAL, resolver.resolve(team(MONTREAL), null));
  }

  @Test
  void resolve_anEmptyPoint_isTheTeamsZone() {
    assertEquals(MONTREAL, resolver.resolve(team(MONTREAL), new Point<G2D>(WGS84)));
  }

  /** Outside every zone (here, off the globe): nothing located, the team's zone. */
  @Test
  void resolve_aPointOutsideEveryZone_isTheTeamsZone() {
    assertTrue(resolver.locate(95.0, 0.0).isEmpty());
    assertEquals(MONTREAL, resolver.resolve(team(MONTREAL), point(WGS84, g(0.0, 95.0))));
  }

  /**
   * At sea, timeshape answers with the nautical zone of the ocean (Etc/GMT±n), not with nothing: a
   * start in the middle of the Atlantic is located, and the team's zone is not used.
   */
  @Test
  void resolve_aPointAtSea_isTheOceansNauticalZone() {
    Optional<ZoneId> atSea = resolver.locate(40.0, -40.0);
    assertEquals(Optional.of(ZoneId.of("Etc/GMT+3")), atSea);
    assertEquals(
        ZoneId.of("Etc/GMT+3"), resolver.resolve(team(MONTREAL), point(WGS84, g(-40, 40))));
  }

  @Test
  void teamZone_withoutAStoredZone_isParis() {
    Team team = new Team();
    team.setTimezone(null);
    assertEquals(PARIS, EventTimezoneResolver.teamZone(team));
  }

  // ─── Ride: start → route → team ───────────────────────────────────────────

  @Test
  void ride_theStartPlaceComesFirst() {
    assertEquals(TOKYO, resolver.ride(team(MONTREAL), place(35.68, 139.76), route(40.71, -74.01)));
  }

  @Test
  void ride_withoutStart_theRouteStart() {
    assertEquals(NEW_YORK, resolver.ride(team(MONTREAL), null, route(40.71, -74.01)));
  }

  @Test
  void ride_aStartPlaceWithoutPosition_fallsThroughToTheRoute() {
    assertEquals(NEW_YORK, resolver.ride(team(MONTREAL), new Place(), route(40.71, -74.01)));
  }

  @Test
  void ride_aDeletedRoute_isSkipped() {
    Route route = route(40.71, -74.01);
    route.setDeleted(true);
    assertEquals(MONTREAL, resolver.ride(team(MONTREAL), null, route));
    assertTrue(resolver.locateRide(null, route).isEmpty());
  }

  @Test
  void ride_nothing_isTheTeamsZone() {
    assertEquals(LONDON, resolver.ride(team(LONDON), null, null));
    assertTrue(resolver.locateRide(null, null).isEmpty());
  }

  /** Unlike the weather's departure, a ride is not located by its first group's route. */
  @Test
  void ride_aGroupRoute_doesNotLocateTheRide() {
    Ride ride = new Ride();
    ride.setTeam(team(MONTREAL));
    RideGroup group = new RideGroup();
    group.setRoute(route(35.68, 139.76));
    ride.getGroups().add(group);
    assertEquals(MONTREAL, resolver.ride(ride));
  }

  // ─── Stages: start → route → previous stage → trip route → team ───────────

  @Test
  void stage_theStartPlaceComesFirst() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(
            List.of(new StagePoints(place(35.68, 139.76), route(40.71, -74.01))),
            route(48.85, 2.35));
    assertEquals(List.of(Optional.of(TOKYO)), zones);
  }

  @Test
  void stage_withoutStart_itsRoute() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(
            List.of(new StagePoints(null, route(40.71, -74.01))), route(48.85, 2.35));
    assertEquals(List.of(Optional.of(NEW_YORK)), zones);
  }

  /**
   * Plan §4: stage 2 in Japan, typed before its place was chosen, takes stage 1's zone — not the
   * trip route's (Paris).
   */
  @Test
  void stage_withoutPoints_inheritsThePreviousStagesZone_beforeTheTripRoute() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(
            List.of(
                new StagePoints(place(35.68, 139.76), null),
                new StagePoints(null, null),
                new StagePoints(null, null)),
            route(48.85, 2.35));
    assertEquals(List.of(Optional.of(TOKYO), Optional.of(TOKYO), Optional.of(TOKYO)), zones);
  }

  @Test
  void stage_aLocatedStage_breaksTheInheritance() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(
            List.of(
                new StagePoints(place(35.68, 139.76), null),
                new StagePoints(null, route(40.71, -74.01)),
                new StagePoints(null, null)),
            null);
    assertEquals(List.of(Optional.of(TOKYO), Optional.of(NEW_YORK), Optional.of(NEW_YORK)), zones);
  }

  @Test
  void stage_firstWithoutPoints_takesTheTripRoute() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(List.of(new StagePoints(null, null)), route(40.71, -74.01));
    assertEquals(List.of(Optional.of(NEW_YORK)), zones);
  }

  @Test
  void stage_nothingAtAll_isEmpty_soTheTeam() {
    List<Optional<ZoneId>> zones =
        resolver.locateStages(
            List.of(new StagePoints(null, null), new StagePoints(null, null)), null);
    assertEquals(List.of(Optional.empty(), Optional.empty()), zones);
  }

  @Test
  void stage_aDeletedStageRoute_isSkipped() {
    Route deleted = route(35.68, 139.76);
    deleted.setDeleted(true);
    List<Optional<ZoneId>> zones =
        resolver.locateStages(List.of(new StagePoints(null, deleted)), route(40.71, -74.01));
    assertEquals(List.of(Optional.of(NEW_YORK)), zones);
  }

  /** On a trip entity: the live stages in their sort order, a deleted one out of the chain. */
  @Test
  void locateStages_ofATrip_followsTheSortOrder_andSkipsTheDeleted() {
    Trip trip = new Trip();
    trip.setTeam(team(MONTREAL));
    trip.setStages(new ArrayList<>());
    TripStage second = stage(trip, 1, null);
    TripStage deleted = stage(trip, 0, place(40.71, -74.01));
    deleted.setDeleted(true);
    TripStage first = stage(trip, 0, place(35.68, 139.76));

    Map<TripStage, Optional<ZoneId>> zones = resolver.locateStages(trip);

    assertEquals(List.of(first, second), List.copyOf(zones.keySet()));
    // The second inherits from the first, not from the deleted New York stage.
    assertEquals(Optional.of(TOKYO), zones.get(second));
  }

  // ─── Trip: first stage → route → team ─────────────────────────────────────

  @Test
  void trip_theFirstStagesZone() {
    assertEquals(
        Optional.of(TOKYO),
        resolver.locateTrip(
            List.of(Optional.of(TOKYO), Optional.of(NEW_YORK)), route(48.85, 2.35)));
  }

  @Test
  void trip_aFirstStageNotLocated_itsRoute() {
    assertEquals(
        Optional.of(NEW_YORK),
        resolver.locateTrip(List.of(Optional.empty()), route(40.71, -74.01)));
  }

  @Test
  void trip_withoutStage_itsRoute() {
    assertEquals(Optional.of(NEW_YORK), resolver.locateTrip(List.of(), route(40.71, -74.01)));
  }

  @Test
  void trip_nothing_isEmpty() {
    assertTrue(resolver.locateTrip(List.of(), null).isEmpty());
  }

  // ─── Instants ─────────────────────────────────────────────────────────────

  @Test
  void groupStart_itsTimeOnTheRidesLocalDate_inTheRidesZone() {
    // 23:30 UTC on June 1st is already June 2nd in Tokyo.
    Instant ride = Instant.parse("2030-06-01T23:30:00Z");
    RideGroup group = new RideGroup();
    group.setTime(LocalTime.of(10, 0));
    assertEquals(
        Instant.parse("2030-06-02T01:00:00Z"),
        EventTimezoneResolver.groupStart(ride, group, TOKYO));
  }

  @Test
  void groupStart_withoutTime_isTheRidesStart() {
    Instant ride = Instant.parse("2030-06-02T07:00:00Z");
    assertEquals(ride, EventTimezoneResolver.groupStart(ride, new RideGroup(), TOKYO));
  }

  /** Paris springs forward on 2030-03-31: 02:30 does not exist, it moves by the gap (03:30). */
  @Test
  void groupStart_inTheSpringGap_isShiftedByTheGap() {
    Instant ride = Instant.parse("2030-03-31T06:00:00Z");
    RideGroup group = new RideGroup();
    group.setTime(LocalTime.of(2, 30));
    assertEquals(
        Instant.parse("2030-03-31T01:30:00Z"),
        EventTimezoneResolver.groupStart(ride, group, PARIS));
  }

  /** Paris falls back on 2030-10-27: 02:30 exists twice, the earlier offset (+02:00) wins. */
  @Test
  void groupStart_inTheAutumnOverlap_takesTheEarlierOffset() {
    Instant ride = Instant.parse("2030-10-27T08:00:00Z");
    RideGroup group = new RideGroup();
    group.setTime(LocalTime.of(2, 30));
    assertEquals(
        Instant.parse("2030-10-27T00:30:00Z"),
        EventTimezoneResolver.groupStart(ride, group, PARIS));
  }

  // ─── startAt, the one reader of a group's departure (docs/LEDGER_*.md API-60) ──

  @Test
  void startAt_theStoredInstantWins_overTheTime() {
    Ride ride = new Ride();
    ride.setTeam(team(PARIS));
    ride.setTimezone(PARIS.getId());
    ride.setDateTime(Instant.parse("2030-06-02T07:00:00Z"));
    RideGroup group = new RideGroup();
    group.setRide(ride);
    group.setTime(LocalTime.of(10, 0));
    group.setStartAt(Instant.parse("2030-06-02T09:15:00Z"));
    assertEquals(Instant.parse("2030-06-02T09:15:00Z"), EventTimezoneResolver.startAt(group));
  }

  @Test
  void startAt_withoutStoredInstant_readsTheTimeInTheRidesStoredZone() {
    Ride ride = new Ride();
    ride.setTeam(team(PARIS));
    ride.setTimezone(TOKYO.getId());
    ride.setDateTime(Instant.parse("2030-06-01T23:30:00Z"));
    RideGroup group = new RideGroup();
    group.setRide(ride);
    group.setTime(LocalTime.of(10, 0));
    // June 2nd in Tokyo, the ride's zone, not the team's.
    assertEquals(Instant.parse("2030-06-02T01:00:00Z"), EventTimezoneResolver.startAt(group));
  }

  @Test
  void startAt_onARowWithoutZone_readsTheTimeInTheTeamsZone() {
    Ride ride = new Ride();
    ride.setTeam(team(MONTREAL));
    ride.setTimezone(null);
    ride.setDateTime(Instant.parse("2030-06-02T12:00:00Z"));
    RideGroup group = new RideGroup();
    group.setRide(ride);
    group.setTime(LocalTime.of(10, 0));
    // 10:00 in Montreal (EDT, UTC-4) — never UTC.
    assertEquals(Instant.parse("2030-06-02T14:00:00Z"), EventTimezoneResolver.startAt(group));
  }

  @Test
  void startAt_withoutTimeNorStoredInstant_isTheRidesStart() {
    Ride ride = new Ride();
    ride.setDateTime(Instant.parse("2030-06-02T07:00:00Z"));
    RideGroup group = new RideGroup();
    group.setRide(ride);
    assertEquals(ride.getDateTime(), EventTimezoneResolver.startAt(group));
  }

  @Test
  void applyGroupStarts_writesEveryGroup() {
    Ride ride = new Ride();
    ride.setDateTime(Instant.parse("2030-06-02T07:00:00Z"));
    RideGroup early = new RideGroup();
    RideGroup late = new RideGroup();
    late.setTime(LocalTime.of(11, 0));
    ride.getGroups().addAll(List.of(early, late));

    EventTimezoneResolver.applyGroupStarts(ride, PARIS);

    assertEquals(Instant.parse("2030-06-02T07:00:00Z"), early.getStartAt());
    assertEquals(Instant.parse("2030-06-02T09:00:00Z"), late.getStartAt());
  }

  @Test
  void sameWallTime_keepsTheWallTime_inTheOtherZone() {
    // 09:30 in Paris (UTC+2 in June) is 09:30 in Montreal (UTC-4).
    assertEquals(
        Instant.parse("2030-06-02T13:30:00Z"),
        EventTimezoneResolver.sameWallTime(Instant.parse("2030-06-02T07:30:00Z"), PARIS, MONTREAL));
  }

  @Test
  void sameWallTime_intoAGap_isShiftedByTheGap() {
    // 02:30 in Tokyo on 2030-03-31 does not exist in Paris that night: 03:30 CEST.
    Instant tokyo = Instant.parse("2030-03-30T17:30:00Z");
    assertEquals(
        Instant.parse("2030-03-31T01:30:00Z"),
        EventTimezoneResolver.sameWallTime(tokyo, TOKYO, PARIS));
  }

  // ─── Fixtures ─────────────────────────────────────────────────────────────

  private static Team team(ZoneId zone) {
    Team team = new Team();
    team.setTimezone(zone.getId());
    return team;
  }

  private static Place place(double lat, double lon) {
    Place place = new Place();
    place.setGeometry(point(WGS84, g(lon, lat)));
    return place;
  }

  private static Route route(double lat, double lon) {
    Route route = new Route();
    route.setStart(point(WGS84, g(lon, lat)));
    return route;
  }

  private static TripStage stage(Trip trip, int sortOrder, Place start) {
    TripStage stage = new TripStage();
    stage.setTrip(trip);
    stage.setSortOrder(sortOrder);
    stage.setStartPlace(start);
    trip.getStages().add(stage);
    return stage;
  }
}
