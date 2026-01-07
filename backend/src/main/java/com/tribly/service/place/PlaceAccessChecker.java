package com.tribly.service.place;

import com.tribly.common.TsidUtils;
import com.tribly.domain.place.Place;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.repository.place.PlaceRepository;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PlaceAccessChecker implements AccessChecker {

  @Inject PlaceRepository placeRepository;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.PLACE;
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
        String placeId = triblyContext.getParam(params, 1);
        if (teamRole == null || !teamRole.isOrganizer() || placeId == null) {
          yield false;
        }
        Place place =
            placeRepository.findByIdAndTeam(TsidUtils.toLong(placeId), team.getId()).orElse(null);
        yield place != null && !place.isDeleted();
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
