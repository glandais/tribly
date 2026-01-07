package com.tribly.service.team;

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
public class UserTeamAccessChecker implements AccessChecker {

  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.USER_TEAM;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
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
