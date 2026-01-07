package com.tribly.service.route;

import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RouteAccessChecker implements AccessChecker {
  @Inject RouteService routeService;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.ROUTE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      // all users can list routes
      // SQL filter will be applied anyway
      case LIST_ALL_TEAMS, LIST -> true;
      case CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = triblyContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getTriblyQuery
        routeService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // organizer in route team
          yield teamRole != null && teamRole.isOrganizer();
        }
      }
      case LEAVE, JOIN -> false;
    };
  }
}
