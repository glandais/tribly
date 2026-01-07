package com.tribly.service.trip;

import com.tribly.domain.team.Team;
import com.tribly.domain.trip.Trip;
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
public class TripAccessChecker implements AccessChecker {
  @Inject TripService tripService;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.TRIP;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    // Check if trips are enabled for the team
    if (team != null && !team.isEnableTrips() && action != ActionType.READ) {
      return false;
    }

    return switch (action) {
      case CREATE -> teamRole != null && teamRole.isOrganizer();
      case READ, UPDATE, DELETE -> {
        String slug = triblyContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getTriblyQuery
        tripService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // organizer in trip team
          yield teamRole != null && teamRole.isOrganizer();
        }
      }
      case JOIN, LEAVE -> {
        String slug = triblyContext.getParam(params, 1);
        if (slug == null || team == null) {
          yield false;
        }
        Trip trip = tripService.findBySlug(team, slug);
        // only members and trip has to be published
        yield teamRole != null && trip.getStatus() == Status.PUBLISHED;
      }
      case LIST, LIST_ALL_TEAMS -> false;
    };
  }
}
