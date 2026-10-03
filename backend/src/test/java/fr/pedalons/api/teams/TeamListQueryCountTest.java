package fr.pedalons.api.teams;

import fr.pedalons.api.AbstractQueryCountTest;
import fr.pedalons.domain.team.Team;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Database-cost budget for the team listing, the source of the member home's « Mes équipes ».
 *
 * <p>Each row carries content counters (upcoming rides and trips, routes, recent posts) that {@code
 * TeamStatsRepository} loads for the whole page at once. Counting per team would be four queries
 * per row; see {@link AbstractQueryCountTest}.
 */
@QuarkusTest
class TeamListQueryCountTest extends AbstractQueryCountTest {

  @Override
  @BeforeEach
  public void setUp() {
    super.setUp();
  }

  private void seedTeams(int count) {
    Instant now = Instant.now();
    for (int i = 0; i < count; i++) {
      // createTeam enrols user1 as ADMIN, so every team is one of "my teams".
      Team team =
          dataService.createTeam(user1, "Budget Team " + i, "budget-team-" + i, Visibility.PUBLIC);
      dataService.createRide(
          team, user1, "Sortie " + i, "sortie-" + i, now.plus(2, ChronoUnit.DAYS));
      dataService.createTrip(team, user1, "Voyage " + i, now.plus(20, ChronoUnit.DAYS));
      dataService.createPost(team, user1, "Nouvelle " + i, now.minus(1, ChronoUnit.DAYS));
      dataService.createRoute(team, user1, "Parcours " + i, Visibility.PUBLIC);
    }
  }

  @Test
  void listMyTeams_costDoesNotScaleWithRowCount() {
    seedTeams(LARGE_PAGE);
    assertFlatQueryCount("GET /api/teams?minRole=MEMBER", asUser1(), "/api/teams?minRole=MEMBER");
  }

  @Test
  void listTeamsAnonymous_costDoesNotScaleWithRowCount() {
    seedTeams(LARGE_PAGE);
    assertFlatQueryCount("GET /api/teams anonymous", anonymous(), "/api/teams");
  }
}
