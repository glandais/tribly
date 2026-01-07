package com.tribly.service.ridetemplate;

import com.tribly.domain.ridetemplate.RideTemplate;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.repository.ridetemplate.RideTemplateRepository;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RideTemplateAccessChecker implements AccessChecker {

  @Inject RideTemplateRepository rideTemplateRepository;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.RIDE_TEMPLATE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null || user == null) {
      return false;
    }
    return switch (action) {
      case LIST, CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = triblyContext.getParam(params, 1);
        if (teamRole == null || !teamRole.isOrganizer() || slug == null) {
          yield false;
        }
        RideTemplate template =
            rideTemplateRepository.findByTeamAndSlug(team.getId(), slug).orElse(null);
        yield template != null && !template.isDeleted();
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
