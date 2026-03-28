package fr.pedalons.service.ridetemplate;

import fr.pedalons.domain.ridetemplate.RideTemplate;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.ridetemplate.RideTemplateRepository;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RideTemplateAccessChecker implements AccessChecker {

  @Inject RideTemplateRepository rideTemplateRepository;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.RIDE_TEMPLATE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null || user == null) {
      return false;
    }
    return switch (action) {
      case LIST, CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = pedalonsContext.getParam(params, 1);
        if (teamRole == null || !teamRole.isOrganizer() || slug == null) {
          yield false;
        }
        RideTemplate template =
            rideTemplateRepository.findByTeamAndSlug(team.getId(), slug).orElse(null);
        yield template != null;
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
