package fr.pedalons.service.trip;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.repository.trip.TripStageRepository;
import fr.pedalons.service.security.AccessChecker;
import fr.pedalons.service.security.Context;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class TripStageAccessChecker implements AccessChecker {

  @Inject TripStageRepository tripStageRepository;
  @Inject TripAccessChecker tripAccessChecker;
  @Inject PedalonsQueryContext pedalonsContext;

  @Override
  public EntityType getType() {
    return EntityType.TRIP_STAGE;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {
    if (action != ActionType.READ) {
      return false;
    }

    Context context = pedalonsContext.getContext(params);
    Team team = context.team();
    String stageSlug = pedalonsContext.getParam(params, 1);

    if (stageSlug == null || team == null) {
      return false;
    }

    TripStage stage = tripStageRepository.findBySlugAndTeam(stageSlug, team.getId()).orElse(null);
    if (stage == null) {
      return false;
    }

    // Delegate to parent trip access check
    return tripAccessChecker.hasRights(
        ActionType.READ, List.of(team.getSlug(), stage.getTrip().getSlug()));
  }
}
