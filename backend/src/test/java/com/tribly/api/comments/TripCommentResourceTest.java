package com.tribly.api.comments;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;

@QuarkusTest
class TripCommentResourceTest extends AbstractCommentResourceTest {

  @Override
  protected String getEntityPath() {
    return "trips";
  }

  @Override
  protected String createEntity(Team team, User creator) {
    Visibility visibility = team.getVisibility();
    return dataService
        .createTrip(team, creator, "Test Trip", Instant.now(), visibility, Status.PUBLISHED, null)
        .getSlug();
  }
}
