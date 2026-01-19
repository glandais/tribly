package com.tribly.api.comments;

import com.tribly.common.TsidUtils;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.comments.response.CommentListResponse;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.enums.EntityType;
import com.tribly.service.comment.CommentService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{teamSlug}/posts/{entitySlug}/comments")
@Tag(name = "Post Comments", description = "Comment operations for posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Transactional
public class PostCommentResource {

  @Inject CommentService commentService;

  @GET
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
  public Response listPostComments(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug) {
    CommentListResponse response =
        commentService.listComments(teamSlug, entitySlug, EntityType.POST);
    return Response.ok(response).build();
  }

  @POST
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
  public Response createPostComment(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug,
      @Valid CommentRequest request) {
    CommentDto comment =
        commentService.createComment(teamSlug, entitySlug, EntityType.POST, request);
    return Response.status(Response.Status.CREATED).entity(comment).build();
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
  public Response deletePostComment(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("entitySlug") String entitySlug,
      @Parameter(description = "Comment ID") @PathParam("commentId") String commentId) {
    commentService.deleteComment(
        teamSlug, entitySlug, EntityType.POST, TsidUtils.toLong(commentId));
    return Response.noContent().build();
  }
}
