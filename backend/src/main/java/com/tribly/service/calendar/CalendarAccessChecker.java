package com.tribly.service.calendar;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.dto.calendar.request.AuthMode;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CalendarAccessChecker implements AccessChecker {

  @Inject TriblyQueryContext triblyContext;

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
          triblyContext.getUser();
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
          user = triblyContext.getUser();
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
      case READ, CREATE, UPDATE, DELETE -> triblyContext.getUserNullable() != null;
      default -> false;
    };
  }
}
