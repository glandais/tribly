package fr.pedalons.api.users;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Budget for the "me" fields. {@code registered} / {@code registeredGroupId} are per-user, which is
 * exactly the kind of field that gets resolved row by row: one {@code findByUserAndRide} per line
 * turns a 20-row feed into 20 extra round-trips and nobody notices until production.
 *
 * <p>These tests seed a page where <em>every</em> row has a participation for the caller — a fixture
 * where nobody is registered would never exercise the lookup at all.
 */
@QuarkusTest
class ParticipationQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedJoinedRides(int count) {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    List<User> participants = List.of(user1, user2, user3, user4, user5);
    for (int i = 0; i < count; i++) {
      Ride ride =
          dataService.createRide(
              team1,
              user1,
              "Joined Ride " + i,
              "joined-ride-" + i,
              base.plusSeconds(i),
              Visibility.PUBLIC,
              Status.PUBLISHED);
      for (int g = 0; g < 2; g++) {
        RideGroup group = dataService.createRideGroup(user1, ride, "Group " + g, g);
        // user1 only joins one of the two groups: joining both is forbidden by joinGroup.
        for (User participant :
            g == 0 ? participants : participants.subList(1, participants.size())) {
          dataService.createParticipation(group, participant);
        }
      }
    }
  }

  private void seedJoinedTrips(int count) {
    Instant base = Instant.now().plus(7, ChronoUnit.DAYS);
    for (int i = 0; i < count; i++) {
      Trip trip =
          dataService.createTrip(
              team1, user1, "Joined Trip " + i, base.plusSeconds(i), Visibility.PUBLIC);
      dataService.createTripStage(user1, trip, "Trip " + i + " Stage", 0);
      dataService.createTripParticipation(trip, user1);
    }
  }

  @Test
  void listAllPublicationsWithMeFields_costDoesNotScaleWithRowCount() {
    seedJoinedRides(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/publications (registered fields)", asUser1(), "/api/publications");
  }

  @Test
  void listParticipatingFilter_costDoesNotScaleWithRowCount() {
    seedJoinedRides(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/publications?participating=true",
        asUser1(),
        "/api/publications?participating=true");
  }

  @Test
  void listMyParticipations_costDoesNotScaleWithRowCount() {
    seedJoinedRides(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/users/me/participations", asUser1(), "/api/users/me/participations");
  }

  @Test
  void listMyParticipationsWithTrips_costDoesNotScaleWithRowCount() {
    seedJoinedTrips(LARGE_PAGE);
    assertFlatQueryCount(
        "GET /api/users/me/participations (trips)", asUser1(), "/api/users/me/participations");
  }
}
