package fr.pedalons.service.place;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.place.PlaceRepository;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class PlaceAccessChecker implements AccessChecker {

  @Inject PlaceRepository placeRepository;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.PLACE;
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
        String placeId = pedalonsContext.getParam(params, 1);
        if (teamRole == null || !teamRole.isOrganizer() || placeId == null) {
          yield false;
        }
        Place place =
            placeRepository.findByIdAndTeam(TsidUtils.toLong(placeId), team.getId()).orElse(null);
        yield place != null;
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
