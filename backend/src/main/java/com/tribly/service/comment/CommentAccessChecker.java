package com.tribly.service.comment;

import com.tribly.domain.comment.Comment;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamRole;
import com.tribly.repository.comment.CommentRepository;
import com.tribly.service.security.AccessChecker;
import com.tribly.service.security.Context;
import com.tribly.service.security.TriblyQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class CommentAccessChecker implements AccessChecker {

  @Inject CommentService commentService;
  @Inject CommentRepository commentRepository;
  @Inject TriblyQueryContext triblyContext;

  @Override
  public EntityType getType() {
    return EntityType.COMMENT;
  }

  @Override
  public boolean hasRights(ActionType action, List<Object> params) {

    Context context = triblyContext.getContext(params);
    Team team = context.team();
    User user = context.user();
    TeamRole teamRole = context.teamRole();

    if (team == null || user == null || teamRole == null) {
      return false;
    }

    return switch (action) {
      case LIST, READ, CREATE -> true;
      case UPDATE, DELETE -> {
        if (params.size() < 4) {
          yield false;
        }
        String slug = triblyContext.getParam(params, 1);
        EntityType entityType = triblyContext.getParam(params, 2);
        Long commentId = triblyContext.getParam(params, 3);
        TeamEntity teamEntity = commentService.getTeamEntity(team, slug, entityType);
        Comment comment = commentRepository.findByTeamIdAndId(team.getId(), commentId).orElse(null);
        if (comment == null) {
          yield false;
        }
        if (!comment.getTeamEntity().getId().equals(teamEntity.getId())) {
          yield false;
        }
        if (teamRole.isOrganizer()) {
          yield true;
        }
        yield comment.getCreatedBy().getId().equals(user.getId());
      }
      case LIST_ALL_TEAMS, JOIN, LEAVE -> false;
    };
  }
}
