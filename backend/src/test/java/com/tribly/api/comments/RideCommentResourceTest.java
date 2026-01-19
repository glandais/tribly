package com.tribly.api.comments;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
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
