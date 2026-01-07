package com.tribly.service.ad;

import com.tribly.domain.ad.Ad;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class AdAccessChecker implements AccessChecker {
  @Inject AdService adService;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.AD;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null || user == null || teamRole == null) {
      // only members can see ads
      return false;
    }
    if (!team.isEnableAds()) {
      return false;
    }
    return switch (action) {
      case LIST, CREATE -> true;
      case READ, UPDATE, DELETE -> {
        String slug = triblyContext.getParam(params, 1);
        if (slug == null) {
          yield false;
        }
        // find with SQL filters, throws 404 if not found
        // see TeamEntityRepository.getTriblyQuery
        Ad ad = adService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // only for admin/ad creator
          yield teamRole.isAdmin() || ad.getCreatedBy().getId().equals(user.getId());
        }
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
