package fr.pedalons.service.team;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
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

@ApplicationScoped
public class UserTeamAccessChecker implements AccessChecker {

  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.USER_TEAM;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (user == null) {
      return false;
    }
    return switch (action) {
      case JOIN -> team != null && team.getVisibility() == Visibility.PUBLIC;
      case READ, LIST, CREATE, UPDATE, DELETE -> teamRole != null && teamRole.isAdmin();
      case LEAVE -> teamRole != null;
      case LIST_ALL_TEAMS -> false;
    };
  }
}
