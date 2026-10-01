package fr.pedalons.service.tag;

import fr.pedalons.domain.team.Team;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

/**
 * A team's tag vocabulary: read by whoever sees the team — tags are displayed to anonymous
 * visitors on public content (plan D19) — and managed by its admins alone (plan D4). Tagging a
 * content is not checked here: it rides on the content's own edit rights (plan D5).
 */
@ApplicationScoped
public class TagAccessChecker implements AccessChecker {

  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.TAG;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {
    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    TeamRole teamRole = context.teamRole();
    if (team == null) {
      return false;
    }
    return switch (action) {
      // Same rule as reading the team itself (TeamAccessChecker).
      case LIST, READ -> team.getVisibility() != Visibility.TEAM || teamRole != null;
      case CREATE, UPDATE, DELETE -> context.user() != null && teamRole == TeamRole.ADMIN;
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
