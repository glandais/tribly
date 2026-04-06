package fr.pedalons.service.common;

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
public class AllPublicationAccessChecker implements AccessChecker {

  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.PUBLICATION;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {
    if (action != ActionType.LIST && action != ActionType.LIST_ALL_TEAMS) {
      return false;
    }
    if (action == ActionType.LIST_ALL_TEAMS) {
      return true;
    }

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null) {
      // all publications, SQL filtered
      return true;
    }
    if (team.getVisibility() != Visibility.TEAM) {
      return true;
    }
    return user != null && teamRole != null;
  }
}
