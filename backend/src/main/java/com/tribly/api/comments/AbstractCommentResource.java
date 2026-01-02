package com.tribly.api.comments;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.TeamEntity;
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

  protected abstract TeamEntity getTeamEntity(String teamSlug, String entitySlug, Long userId);

  protected abstract String getEntityType();

  @RolesAllowed("user")
  public Response listComments(String teamSlug, String entitySlug) {
    Long userId = getCurrentUserId();
    TeamEntity entity = getTeamEntity(teamSlug, entitySlug, userId);
    CommentListResponse response = commentService.listComments(teamSlug, entity.getId(), userId);
    return Response.ok(response).build();
  }

  @RolesAllowed("user")
  public Response createComment(String teamSlug, String entitySlug, @Valid CommentRequest request) {
    Long userId = getCurrentUserId();
    TeamEntity entity = getTeamEntity(teamSlug, entitySlug, userId);
    CommentDto comment = commentService.createComment(teamSlug, entity, request, userId);
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
    Long userId = getCurrentUserId();
    commentService.deleteComment(teamSlug, TsidUtils.toLong(commentId), userId);
    return Response.noContent().build();
  }
}
