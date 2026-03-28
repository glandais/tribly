package fr.pedalons.service.calendar;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.calendar.request.AuthMode;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CalendarAccessChecker implements AccessChecker {

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject CalendarService calendarService;

  @Inject TeamService teamService;

  @Inject UserTeamRepository userTeamRepository;

  @Override
  public EntityType getType() {
    return EntityType.CALENDAR;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {
    return switch (action) {
      case LIST_ALL_TEAMS -> {
        AuthMode authMode = (AuthMode) params.getFirst();
        if (authMode == AuthMode.TOKEN) {
          calendarService.validateToken((String) params.get(1));
        } else {
          pedalonsContext.getUser();
        }
        yield true;
      }
      case LIST -> {
        AuthMode authMode = (AuthMode) params.getFirst();
        User user;
        String teamSlug;
        if (authMode == AuthMode.TOKEN) {
          user = calendarService.validateToken((String) params.get(1));
          teamSlug = (String) params.get(2);
        } else {
          user = pedalonsContext.getUser();
          teamSlug = (String) params.get(1);
        }
        Team team = teamService.getTeam(teamSlug);
        TeamRole teamRole =
            userTeamRepository
                .findByUserAndTeam(user.getId(), team.getId())
                .map(UserTeam::getRole)
                .orElse(null);
        yield teamRole != null;
      }
      case READ, CREATE, UPDATE, DELETE -> pedalonsContext.getUserNullable() != null;
      default -> false;
    };
  }
}
