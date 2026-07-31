package fr.pedalons.service.team;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class UserTeamAccessChecker implements AccessChecker {

  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.USER_TEAM;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (user == null) {
      return false;
    }
    return switch (action) {
      case JOIN -> team != null && team.getVisibility() != Visibility.TEAM && team.isJoinable();
      // LIST is deliberately its own branch. Folding it back in with CREATE/UPDATE/DELETE would
      // hand every member the right to add and remove members, which is the same word for a very
      // different thing.
      //
      // An organiser reads the roster whatever the team decided: designating a ride group's leader
      // needs a list of candidates, and RideService rejects a leader who is not a member. Everyone
      // else needs the team to have opened its directory. What each of them then *sees* is decided
      // in TeamMembershipService.getTeamMembers, not here.
      case LIST ->
          teamRole != null
              && (teamRole.isOrganizer() || (team != null && team.isEnableMemberDirectory()));
      case READ, CREATE, UPDATE, DELETE -> teamRole != null && teamRole.isAdmin();
      case LEAVE -> teamRole != null;
      case LIST_ALL_TEAMS -> false;
    };
  }
}
