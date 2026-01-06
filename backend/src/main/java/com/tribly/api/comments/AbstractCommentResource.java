package com.tribly.api.comments;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.comments.response.CommentListResponse;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.comment.CommentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public abstract class AbstractCommentResource extends AbstractAuthenticatedResource {

  @Inject CommentService commentService;

  protected abstract TeamEntity getTeamEntity(Team team, String entitySlug, User user);

  protected abstract String getEntityType();

  @RolesAllowed("user")
  public Response listComments(String teamSlug, String entitySlug) {
    Team team = teamService.getTeam(teamSlug);
    User user = getCurrentUser();
    TeamEntity entity = getTeamEntity(team, entitySlug, user);
    CommentListResponse response = commentService.listComments(team, entity.getId(), user);
    return Response.ok(response).build();
  }

  @RolesAllowed("user")
  public Response createComment(String teamSlug, String entitySlug, @Valid CommentRequest request) {
    Team team = teamService.getTeam(teamSlug);
    User user = getCurrentUser();
    TeamEntity entity = getTeamEntity(team, entitySlug, user);
    CommentDto comment = commentService.createComment(entity, request, user);
    return Response.created(
            URI.create(
                "/api/teams/"
                    + teamSlug
                    + "/"
                    + getEntityType()
                    + "/"
                    + entitySlug
                    + "/comments/"
                    + comment.id()))
        .entity(comment)
        .build();
  }

  @RolesAllowed("user")
  public Response deleteComment(String teamSlug, String commentId) {
    Team team = teamService.getTeam(teamSlug);
    User user = getCurrentUser();
    commentService.deleteComment(team, TsidUtils.toLong(commentId), user);
    return Response.noContent().build();
  }
}
