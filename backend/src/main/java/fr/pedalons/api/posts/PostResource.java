package fr.pedalons.api.posts;

import fr.pedalons.dto.common.request.SlugChangeRequest;
import fr.pedalons.dto.error.ErrorResponse;
import fr.pedalons.dto.posts.request.PostRequest;
import fr.pedalons.dto.posts.response.PostDto;
import fr.pedalons.service.post.PostService;
import jakarta.annotation.security.PermitAll;
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

@Path("/api/teams/{teamSlug}/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Posts", description = "Post management and participation operations")
public class PostResource {

  @Inject PostService postService;

  @POST
  @Operation(summary = "Create post", description = "Create a new post with optional groups")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Post created successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team member",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response createPost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid PostRequest request) {

    PostDto post = postService.createPost(teamSlug, request);

    return Response.status(Response.Status.CREATED).entity(post).build();
  }

  @GET
  @Path("/{postSlug}")
  @PermitAll
  @Operation(
      summary = "Get post details",
      description = "Get detailed post information including groups")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post retrieved successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or post not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getPost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {

    PostDto post = postService.getDto(teamSlug, postSlug);
    return Response.ok(post).build();
  }

  @PUT
  @Path("/{postSlug}")
  @Transactional
  @Operation(
      summary = "Update post",
      description = "Update post information. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post updated successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to update this post",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or post not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response updatePost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug,
      @Valid PostRequest request) {

    PostDto updatedPost = postService.updatePost(teamSlug, postSlug, request);

    return Response.ok(updatedPost).build();
  }

  @DELETE
  @Path("/{postSlug}")
  @Operation(
      summary = "Delete post",
      description = "Soft delete a post. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Post deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this post",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or post not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response deletePost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {

    postService.deletePost(teamSlug, postSlug);
    return Response.noContent().build();
  }

  @POST
  @Path("/{postSlug}/undelete")
  @Operation(
      operationId = "undeletePost",
      summary = "Restore post",
      description = "Restore a soft-deleted post. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Post restored successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to restore this post",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or post not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response undeletePost(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {
    PostDto dto = postService.undeletePost(teamSlug, postSlug);
    return Response.ok(dto).build();
  }

  @PATCH
  @Path("/{postSlug}/slug")
  @Operation(
      operationId = "changePostSlug",
      summary = "Change post slug",
      description = "Change post URL slug. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = PostDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid slug format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Current post URL slug") @PathParam("postSlug") String currentSlug,
      @Valid SlugChangeRequest request) {

    PostDto post = postService.updateSlug(teamSlug, currentSlug, request.slug());
    return Response.ok(post).build();
  }
}
