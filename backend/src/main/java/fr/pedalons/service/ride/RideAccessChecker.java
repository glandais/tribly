package fr.pedalons.service.ride;

import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RideAccessChecker implements AccessChecker {

  @Inject RideService rideService;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.RIDE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      case CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = pedalonsContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getPedalonsQuery
        rideService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // organizer in ride team
          yield teamRole != null && teamRole.isOrganizer();
        }
      }
      case JOIN, LEAVE -> {
        String slug = pedalonsContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        Ride ride = rideService.findBySlug(team, slug);
        // only members and ride has to be published
        yield teamRole != null && ride.getStatus() == Status.PUBLISHED;
      }
      case LIST, LIST_ALL_TEAMS -> false;
    };
  }
}
