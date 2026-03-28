package fr.pedalons.api.comments;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.Visibility;
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
