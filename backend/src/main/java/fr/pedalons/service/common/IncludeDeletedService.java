package fr.pedalons.service.common;

import fr.pedalons.domain.team.Team;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class IncludeDeletedService {

  @Inject PedalonsQueryContext pedalonsQueryContext;

  public boolean isTeamEntityIncludeDeleted(@Nullable Team team) {
    TeamRole teamRole = pedalonsQueryContext.getContext(team).teamRole();
    if (teamRole == null) {
      return false;
    }
    return teamRole.isAdmin();
  }
}
