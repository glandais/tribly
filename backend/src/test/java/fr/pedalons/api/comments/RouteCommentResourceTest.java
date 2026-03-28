package fr.pedalons.api.comments;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class RouteCommentResourceTest extends AbstractCommentResourceTest {

  @Override
  protected String getEntityPath() {
    return "routes";
  }

  @Override
  protected String createEntity(Team team, User creator) {
    return dataService.createRoute(team, creator, "Test Route", team.getVisibility()).getSlug();
  }
}
