package com.tribly.service.post;

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
public class PostAccessChecker implements AccessChecker {

  @Inject PostService postService;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.POST;
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
        postService.findBySlug(team, slug);
        if (action == ActionType.READ) {
          // filtering done via SQL
          yield true;
        } else {
          // organizer in post team
          yield teamRole != null && teamRole.isOrganizer();
        }
      }
      case LIST, LIST_ALL_TEAMS, LEAVE, JOIN -> false;
    };
  }
}
