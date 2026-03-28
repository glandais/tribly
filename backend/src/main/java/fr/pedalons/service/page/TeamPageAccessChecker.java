package fr.pedalons.service.page;

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
public class TeamPageAccessChecker implements AccessChecker {
  @Inject TeamPageService teamPageService;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.TEAM_PAGE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null) {
      return false;
    }
    return switch (action) {
      case LIST, CREATE -> teamRole == TeamRole.ADMIN;
      case READ, UPDATE, DELETE -> {
        String slug = pedalonsContext.getParam(params, 1);
        if (slug == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getPedalonsQuery
        teamPageService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // admin in page team
          yield teamRole != null && teamRole.isAdmin();
        }
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
