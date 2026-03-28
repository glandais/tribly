package fr.pedalons.api.comments;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;

@QuarkusTest
class RideCommentResourceTest extends AbstractCommentResourceTest {

  @Override
  protected String getEntityPath() {
    return "rides";
  }

  @Override
  protected String createEntity(Team team, User creator) {
    Visibility visibility = team.getVisibility();
    String slug = "test-ride-" + System.nanoTime();
    return dataService
        .createRide(team, creator, "Test Ride", slug, Instant.now(), visibility, Status.PUBLISHED)
        .getSlug();
  }
}
