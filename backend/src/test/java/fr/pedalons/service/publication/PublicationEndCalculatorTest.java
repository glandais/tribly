package fr.pedalons.service.publication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * The rule of {@link PublicationEndCalculator}, on entities held in memory (docs/LEDGER_*.md
 * API-85). The values it stores at each entry point are covered by {@code PublicationEndStoredTest}.
 */
class PublicationEndCalculatorTest {

  private static final Instant START = Instant.parse("2026-06-07T07:00:00Z");

  private final PublicationEndCalculator calculator = new PublicationEndCalculator();

  private static Route route(float meters) {
    Route route = new Route();
    route.setDistance(meters);
    return route;
  }

  private static Ride ride() {
    Ride ride = new Ride();
    ride.setDateTime(START);
    return ride;
  }

  private static RideGroup group(
      Ride ride, @Nullable LocalTime time, @Nullable Route route, @Nullable Float speed) {
    RideGroup group = new RideGroup(null, ride, "G");
    group.setTime(time);
    group.setRoute(route);
    group.setAverageSpeed(speed);
    ride.addGroup(group);
    return group;
  }

  @Test
  void theDefaultDuration_isThreeHours() {
    assertEquals(Duration.ofHours(3), PublicationEndCalculator.DEFAULT_DURATION);
  }

  @Test
  void aRideWithoutGroup_lastsTheDefaultDuration() {
    assertEquals(START.plus(Duration.ofHours(3)), calculator.rideEnd(ride()));
  }

  @Test
  void aGroup_endsAfterItsRoute_atItsSpeed() {
    Ride ride = ride();
    group(ride, null, route(50_000), 25f);
    assertEquals(START.plus(Duration.ofHours(2)), calculator.rideEnd(ride));
  }

  @Test
  void aGroupWithoutRoute_fallsBackOnTheRidesRoute() {
    Ride ride = ride();
    ride.setRoute(route(100_000));
    group(ride, null, null, 20f);
    assertEquals(START.plus(Duration.ofHours(5)), calculator.rideEnd(ride));
  }

  @Test
  void aGroupWithoutSpeedOrRoute_takesTheDefaultDuration() {
    Ride noSpeed = ride();
    group(noSpeed, null, route(10_000), null);
    assertEquals(START.plus(Duration.ofHours(3)), calculator.rideEnd(noSpeed));

    Ride noRoute = ride();
    group(noRoute, null, null, 30f);
    assertEquals(START.plus(Duration.ofHours(3)), calculator.rideEnd(noRoute));
  }

  @Test
  void aDeletedRoute_countsAsNoRoute() {
    Ride ride = ride();
    Route deleted = route(200_000);
    deleted.setDeleted(true);
    group(ride, null, deleted, 20f);
    assertEquals(START.plus(Duration.ofHours(3)), calculator.rideEnd(ride));
  }

  @Test
  void theRideEnds_withItsLatestGroup_eachFromItsOwnStart() {
    Ride ride = ride();
    group(ride, null, route(50_000), 25f); // 07:00 → 09:00
    // Its stored start_at: 08:00 → 10:00 at 30 km/h.
    group(ride, LocalTime.of(10, 0), route(60_000), 30f)
        .setStartAt(Instant.parse("2026-06-07T08:00:00Z"));
    assertEquals(Instant.parse("2026-06-07T10:00:00Z"), calculator.rideEnd(ride));
  }

  /** The stored start_at is the one departure every reader shares (docs/LEDGER_*.md API-60). */
  @Test
  void aGroupsStoredStart_winsOverItsTime() {
    Ride ride = ride();
    ride.setTimezone("Europe/Paris");
    RideGroup group = group(ride, LocalTime.of(9, 0), route(50_000), 25f);
    group.setStartAt(Instant.parse("2026-06-07T11:00:00Z"));
    assertEquals(Instant.parse("2026-06-07T13:00:00Z"), calculator.rideEnd(ride));
  }

  /** A row an older backend wrote without start_at: its time in the ride's stored zone. */
  @Test
  void aGroupWithoutStoredStart_readsItsTimeInTheRidesZone() {
    Ride ride = ride();
    ride.setTimezone("Asia/Tokyo");
    // 16:00 Tokyo (UTC+9) on June 7th is 07:00 UTC; 50 km at 25 km/h → 09:00 UTC.
    group(ride, LocalTime.of(16, 0), route(50_000), 25f);
    assertEquals(Instant.parse("2026-06-07T09:00:00Z"), calculator.rideEnd(ride));
  }

  @Test
  void aTrip_endsWithItsLatestLiveStage_andEachStageGetsItsOwnEnd() {
    Trip trip = new Trip();
    trip.setDateTime(START);
    TripStage first = new TripStage(null, trip, "J1", "j1");
    first.setDateTime(START);
    first.setRoute(route(80_000));
    first.setAverageSpeed(20f);
    TripStage second = new TripStage(null, trip, "J2", "j2");
    second.setDateTime(START.plus(Duration.ofDays(1)));
    TripStage dropped = new TripStage(null, trip, "J3", "j3");
    dropped.setDateTime(START.plus(Duration.ofDays(5)));
    dropped.setDeleted(true);
    trip.addStage(first);
    trip.addStage(second);
    trip.addStage(dropped);

    PublicationEndCalculator.TripEnds ends = calculator.tripEnds(trip);

    assertEquals(START.plus(Duration.ofDays(1)).plus(Duration.ofHours(3)), ends.end());
    assertEquals(START.plus(Duration.ofHours(4)), ends.stages().get(first));
    assertFalse(ends.stages().containsKey(dropped));
  }

  @Test
  void aTripWithoutStage_lastsTheDefaultDuration() {
    Trip trip = new Trip();
    trip.setDateTime(START);
    assertEquals(START.plus(Duration.ofHours(3)), calculator.tripEnds(trip).end());
  }

  @Test
  void refresh_writesOnlyWhatChanged_andLeavesAPostAlone() {
    Ride ride = ride();
    assertTrue(calculator.refresh(ride));
    assertEquals(START.plus(Duration.ofHours(3)), ride.getEndDateTime());
    assertFalse(calculator.refresh(ride));

    Post post = new Post();
    post.setDateTime(START);
    assertFalse(calculator.refresh(post));
    assertNull(post.getEndDateTime());
  }

  @Test
  void effectiveEnd_fallsBackOnTheDefaultDuration_whenNothingIsStored() {
    Ride ride = ride();
    assertEquals(START.plus(Duration.ofHours(3)), PublicationEndCalculator.effectiveEnd(ride));
    ride.setEndDateTime(START.plus(Duration.ofHours(1)));
    assertEquals(START.plus(Duration.ofHours(1)), PublicationEndCalculator.effectiveEnd(ride));
  }
}
