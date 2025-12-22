package com.tribly.api.posts;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.posts.request.PostRequest;
import com.tribly.dto.posts.response.PostDto;
import com.tribly.dto.posts.response.PostListResponse;
import com.tribly.enums.Status;
import com.tribly.service.post.PostService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.LocalDateTime;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{slug}/posts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Posts", description = "Post management and participation operations")
public class PostResource extends AbstractAuthenticatedResource {

  @Inject PostService postService;

  @GET
  @PermitAll
  @Operation(
      summary = "List posts",
      description = "Get paginated list of posts for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Posts retrieved successfully",
        content = @Content(schema = @Schema(implementation = PostListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listPosts(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Status filter") @QueryParam("status") @Nullable Status status,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();

    LocalDateTime from = fromStr != null ? LocalDateTime.parse(fromStr) : null;
    LocalDateTime to = toStr != null ? LocalDateTime.parse(toStr) : null;

    PostListResponse posts = postService.listPosts(slug, userId, from, to, status, page, size);

    return Response.ok(posts).build();
  }

  @POST
  @RolesAllowed("user")
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
  public Response createPost(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid PostRequest request) {
    Long userId = getCurrentUserId();

    PostDto post = postService.createPost(slug, request, userId);

    return Response.created(URI.create("/api/teams/" + slug + "/posts/" + post.slug()))
        .entity(post)
        .build();
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
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {
    Long userId = getCurrentUserIdOrNull();
    PostDto post = postService.getPostDetail(slug, postSlug, userId);
    return Response.ok(post).build();
  }

  @PUT
  @Path("/{postSlug}")
  @Transactional
  @RolesAllowed("user")
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
  public Response updatePost(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug,
      @Valid PostRequest request) {

    Long userId = getCurrentUserId();

    PostDto updatedPost = postService.updatePost(slug, postSlug, request, userId);

    return Response.ok(updatedPost).build();
  }

  @DELETE
  @Path("/{postSlug}")
  @RolesAllowed("user")
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
  public Response deletePost(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Post URL slug") @PathParam("postSlug") String postSlug) {
    Long userId = getCurrentUserId();
    postService.deletePost(slug, postSlug, userId);
    return Response.noContent().build();
  }
}
