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
public class TeamAccessChecker implements AccessChecker {

  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.TEAM;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {
    // For CREATE and LIST, there's no team slug - get user context only
    if (action == ActionType.CREATE) {
      return triblyContext.getUserNullable() != null;
    }
    if (action == ActionType.LIST) {
      return true;
    }

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      case CREATE, LIST -> true; // Handled above
      case READ -> {
        if (team == null) {
          yield false;
        }
        if (team.getVisibility() == Visibility.PUBLIC) {
          yield true;
        }
        yield teamRole != null;
      }
      case UPDATE, DELETE -> teamRole == TeamRole.ADMIN;
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
