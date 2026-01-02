package com.tribly.api.comments;

import com.tribly.domain.common.TeamEntity;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.comments.response.CommentListResponse;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.service.post.PostService;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{slug}/posts/{entitySlug}/comments")
@Tag(name = "Post Comments", description = "Comment operations for posts")
public class PostCommentResource extends AbstractCommentResource {

  @Inject PostService postService;

  @Override
  protected TeamEntity getTeamEntity(String teamSlug, String entitySlug, Long userId) {
    return postService.getPost(teamSlug, entitySlug, userId);
  }

  @Override
  protected String getEntityType() {
    return "posts";
  }

  @GET
  @Override
  @Operation(operationId = "listPostComments", summary = "List post comments")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Comments retrieved",
        content = @Content(schema = @Schema(implementation = CommentListResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "Not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listComments(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug) {
    return super.listComments(teamSlug, entitySlug);
  }

  @POST
  @Override
  @Operation(operationId = "createPostComment", summary = "Create post comment")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Comment created",
        content = @Content(schema = @Schema(implementation = CommentDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createComment(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug,
      CommentRequest request) {
    return super.createComment(teamSlug, entitySlug, request);
  }

  @DELETE
  @Path("/{commentId}")
  @Operation(operationId = "deletePostComment", summary = "Delete post comment")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Comment deleted"),
    @APIResponse(
        responseCode = "403",
        description = "Cannot delete",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteComment(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug,
      @Parameter(description = "Comment ID") @PathParam("commentId") String commentId) {
    return super.deleteComment(teamSlug, commentId);
  }
}
