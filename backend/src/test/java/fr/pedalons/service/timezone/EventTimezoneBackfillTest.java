package fr.pedalons.service.timezone;

import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.api.AbstractResourceTest;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The startup backfill of the rows the previous release writes during a start-first deploy
 * (docs/LEDGER_*.md API-60, plan §8 step 4): null zones get the zone their chain resolves (the
 * team's for what has no chain), at constant instant; null group starts are computed in their
 * ride's zone. What is already filled is kept, and a second run finds nothing.
 */
@QuarkusTest
class EventTimezoneBackfillTest extends AbstractResourceTest {

  private static final Instant START = Instant.parse("2030-06-02T00:00:00Z");

  @Inject EventTimezoneBackfill backfill;

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
    dataService.setTeamModules(team1, true, true);
  }

  @Test
  void runZones_fillsTheNullZones_withTheResolvedOne_atConstantInstant() {
    Place tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
    Ride located = dataService.createRide(team1, user1, "Située", "situee", START);
    dataService.setRidePlaces(located, tokyo, null);
    dataService.setTimezone(located.getId(), null);
    Ride placeless = dataService.createRide(team1, user1, "Sans lieu", "sans-lieu", START);
    dataService.setTimezone(placeless.getId(), null);
    Post post = dataService.createPost(team1, user1, "Billet", START);
    dataService.setTimezone(post.getId(), null);
    Ride kept = dataService.createRide(team1, user1, "Déjà", "deja", START);
    dataService.setTimezone(kept.getId(), "America/Montreal");

    backfill.runZones();
    assertEquals(0, backfill.runZones());

    assertEquals("Asia/Tokyo", dataService.getTimezone(located.getId()));
    assertEquals("Europe/Paris", dataService.getTimezone(placeless.getId()));
    assertEquals("Europe/Paris", dataService.getTimezone(post.getId()));
    assertEquals("America/Montreal", dataService.getTimezone(kept.getId()));
    // At constant instant: the instant is the only truth an old row has.
    assertEquals(START, dataService.getDateTime(located.getId()));
  }

  @Test
  void runZones_aStageWithoutPoints_inheritsThePreviousStage_andTheTripItsFirstStage() {
    Place tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
    Trip trip = dataService.createTrip(team1, user1, "Voyage", START);
    TripStage j1 = dataService.createTripStage(user1, trip, "J1", 0, START);
    TripStage j2 = dataService.createTripStage(user1, trip, "J2", 1, START.plusSeconds(86_400));
    dataService.setTripStagePlaces(j1, tokyo, null);
    dataService.setTimezone(trip.getId(), null);
    dataService.setTimezone(j1.getId(), null);
    dataService.setTimezone(j2.getId(), null);

    backfill.runZones();

    assertEquals("Asia/Tokyo", dataService.getTimezone(j1.getId()));
    assertEquals("Asia/Tokyo", dataService.getTimezone(j2.getId()));
    assertEquals("Asia/Tokyo", dataService.getTimezone(trip.getId()));
  }

  @Test
  void runZones_aTeamInAnotherZone_givesItsZone() {
    dataService.setTeamTimezone(team1, "America/Montreal");
    Post post = dataService.createPost(team1, user1, "Billet", START);
    dataService.setTimezone(post.getId(), null);

    backfill.runZones();

    assertEquals("America/Montreal", dataService.getTimezone(post.getId()));
  }

  @Test
  void runGroupStarts_fillsTheNullStarts_inTheRidesZone() {
    Place tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
    Ride ride = dataService.createRide(team1, user1, "Groupes", "groupes", START);
    dataService.setRidePlaces(ride, tokyo, null);
    dataService.setTimezone(ride.getId(), "Asia/Tokyo");
    RideGroup withTime = dataService.createRideGroup(user1, ride, "Tard", 0);
    dataService.setGroupTime(withTime.getId(), LocalTime.of(10, 0));
    RideGroup withoutTime = dataService.createRideGroup(user1, ride, "Avec", 1);
    RideGroup kept = dataService.createRideGroup(user1, ride, "Déjà", 2);
    Instant keptStart = Instant.parse("2030-06-02T03:00:00Z");
    dataService.setGroupStartAt(kept.getId(), keptStart);

    backfill.runGroupStarts();
    assertEquals(0, backfill.runGroupStarts());

    // 00:00 UTC is 09:00 in Tokyo, on June 2nd: 10:00 there is 01:00 UTC.
    assertEquals(
        Instant.parse("2030-06-02T01:00:00Z"), dataService.getGroupStartAt(withTime.getId()));
    assertEquals(START, dataService.getGroupStartAt(withoutTime.getId()));
    assertEquals(keptStart, dataService.getGroupStartAt(kept.getId()));
  }

  /** The zones first: a group of a ride without a zone is read in the zone the ride gets. */
  @Test
  void bothSteps_inOrder_readTheGroupInTheRidesNewZone() {
    Place tokyo = dataService.createPlaceAt(team1, user1, "Shibuya", 35.66, 139.70);
    Ride ride = dataService.createRide(team1, user1, "Ancienne", "ancienne", START);
    dataService.setRidePlaces(ride, tokyo, null);
    dataService.setTimezone(ride.getId(), null);
    RideGroup group = dataService.createRideGroup(user1, ride, "Tard", 0);
    dataService.setGroupTime(group.getId(), LocalTime.of(10, 0));

    backfill.runZones();
    backfill.runGroupStarts();

    assertEquals(Instant.parse("2030-06-02T01:00:00Z"), dataService.getGroupStartAt(group.getId()));
  }
}
