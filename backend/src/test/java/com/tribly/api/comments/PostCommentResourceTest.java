package com.tribly.api.comments;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Status;
import com.tribly.enums.Visibility;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Instant;

@QuarkusTest
class PostCommentResourceTest extends AbstractCommentResourceTest {

  @Override
  protected String getEntityPath() {
    return "posts";
  }

  @Override
  protected String createEntity(Team team, User creator) {
    Visibility visibility = team.getVisibility();
    return dataService
        .createPost(team, creator, "Test Post", Instant.now(), visibility, Status.PUBLISHED, null)
        .getSlug();
  }
}
