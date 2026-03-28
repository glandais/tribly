package fr.pedalons.api.comments;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.dto.comments.response.CommentDto;
import fr.pedalons.dto.comments.response.CommentListResponse;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.enums.EntityType;
import fr.pedalons.service.comment.CommentService;
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

@Path("/api/teams/{teamSlug}/rides/{entitySlug}/comments")
@Tag(name = "Ride Comments", description = "Comment operations for rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
@Transactional
public class RideCommentResource {

  @Inject CommentService commentService;

  @GET
  @Operation(operationId = "listRideComments", summary = "List ride comments")
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
  public Response listRideComments(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("entitySlug") String entitySlug) {
    CommentListResponse response =
        commentService.listComments(teamSlug, entitySlug, EntityType.RIDE);
    return Response.ok(response).build();
  }

  @POST
  @Operation(operationId = "createRideComment", summary = "Create ride comment")
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
  public Response createRideComment(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("entitySlug") String entitySlug,
      @Valid CommentRequest request) {
    CommentDto comment =
        commentService.createComment(teamSlug, entitySlug, EntityType.RIDE, request);
    return Response.status(Response.Status.CREATED).entity(comment).build();
  }

  @DELETE
  @Path("/{commentId}")
  @Operation(operationId = "deleteRideComment", summary = "Delete ride comment")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Comment deleted"),
    @APIResponse(
        responseCode = "403",
        description = "Cannot delete",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteRideComment(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("entitySlug") String entitySlug,
      @Parameter(description = "Comment ID") @PathParam("commentId") String commentId) {
    commentService.deleteComment(
        teamSlug, entitySlug, EntityType.RIDE, TsidUtils.toLong(commentId));
    return Response.noContent().build();
  }
}
