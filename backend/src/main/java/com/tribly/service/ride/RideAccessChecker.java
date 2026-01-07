package com.tribly.service.ride;

import com.tribly.domain.ride.Ride;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Status;
import com.tribly.enums.TeamRole;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class RideAccessChecker implements AccessChecker {

  @Inject RideService rideService;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.RIDE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      case CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = triblyContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getTriblyQuery
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
        String slug = triblyContext.getParam(params, 1);
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
