package com.tribly.service.asset;

import com.tribly.domain.asset.Asset;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.repository.asset.AssetRepository;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.SecurityVerifier;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AssetAccessChecker implements AccessChecker {

  @Inject AssetRepository assetRepository;

  @Inject SecurityVerifier securityVerifier;

  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.ASSET;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    return switch (action) {
      case CREATE -> teamRole != null;
      case READ, UPDATE, DELETE -> {
        Long assetId = triblyContext.getParam(params, 1);
        if (assetId == null) {
          yield false;
        }
        Asset asset = assetRepository.findByIdOptional(assetId).orElse(null);
        if (asset == null || asset.isDeleted()) {
          yield false;
        }
        team = asset.getTeam();
        TeamEntity teamEntity = asset.getTeamEntity();
        if (teamEntity == null) {
          // Asset not yet attached to entity - only organizers can see/modify
          yield user != null && teamRole != null && asset.getCreatedBy().getId().equals(user.getId());
        }
        // User can read asset if he has rights to see related entity
        securityVerifier.verifyAccess(
            teamEntity.getEntityType(),
            ActionType.READ,
            // we always expect slug as first parameter for team entities
            List.of(team.getSlug(), teamEntity.getSlug()));
        if (action == ActionType.READ) {
          yield true;
        }
        // UPDATE/DELETE require organizer
        yield teamRole != null && teamRole.isOrganizer();
      }
      case LIST, LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
