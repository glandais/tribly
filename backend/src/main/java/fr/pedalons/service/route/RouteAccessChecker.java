package fr.pedalons.service.route;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RouteAccessChecker implements AccessChecker {
  @Inject RouteService routeService;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.ROUTE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    if (action == ActionType.LIST_ALL_TEAMS) {
      return true;
    }
    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      // all users can list routes
      // SQL filter will be applied anyway
      case LIST_ALL_TEAMS, LIST -> true;
      case CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = pedalonsContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getPedalonsQuery
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
