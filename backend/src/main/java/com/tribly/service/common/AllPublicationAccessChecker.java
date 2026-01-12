package com.tribly.service.common;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AllPublicationAccessChecker implements AccessChecker {

  @Inject TriblyQueryContext triblyContext;

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

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null) {
      // all publications, SQL filtered
      return true;
    }
    if (team.getVisibility() == Visibility.PUBLIC) {
      return true;
    }
    return user != null && teamRole != null;
  }
}
