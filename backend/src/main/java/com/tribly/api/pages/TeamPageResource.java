package com.tribly.api.pages;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.pages.request.ReorderPagesRequest;
import com.tribly.dto.pages.request.TeamPageRequest;
import com.tribly.dto.pages.response.TeamPageDto;
import com.tribly.dto.pages.response.TeamPageSummaryDto;
import com.tribly.service.page.TeamPageService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

@Path("/api/teams/{slug}/pages")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Team Pages", description = "Team page management operations")
public class TeamPageResource extends AbstractAuthenticatedResource {

  @Inject TeamPageService teamPageService;

  @GET
  @PermitAll
  @Operation(summary = "List team pages", description = "Get list of additional pages for a team")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Pages retrieved successfully",
        content = @Content(schema = @Schema(implementation = TeamPageSummaryDto[].class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listPages(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug) {
    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(slug);
    List<TeamPageSummaryDto> pages = teamPageService.listPages(team, user);
    return Response.ok(pages).build();
  }

  @GET
  @Path("/{pageSlug}")
  @PermitAll
  @Operation(summary = "Get page details", description = "Get detailed page information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Page retrieved successfully",
        content = @Content(schema = @Schema(implementation = TeamPageDto.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to view this page",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or page not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getPage(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Page URL slug") @PathParam("pageSlug") String pageSlug) {
    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(teamSlug);
    TeamPageDto page = teamPageService.getDto(team, pageSlug, user);
    return Response.ok(page).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(
      summary = "Create page",
      description =
          "Create a new team page. Requires admin permissions. Maximum 3 additional pages per"
              + " team.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Page created successfully",
        content = @Content(schema = @Schema(implementation = TeamPageDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid request or maximum pages reached",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createPage(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid TeamPageRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    TeamPageDto page = teamPageService.createPage(team, request, user);
    return Response.created(
            URI.create("/api/teams/" + page.team().slug() + "/pages/" + page.slug()))
        .entity(page)
        .build();
  }

  @PUT
  @Path("/{pageSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Update page",
      description = "Update page information. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Page updated successfully",
        content = @Content(schema = @Schema(implementation = TeamPageDto.class))),
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
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or page not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updatePage(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Page URL slug") @PathParam("pageSlug") String pageSlug,
      @Valid TeamPageRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    TeamPageDto updatedPage = teamPageService.updatePage(team, pageSlug, request, user);
    return Response.ok(updatedPage).build();
  }

  @DELETE
  @Path("/{pageSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Delete page",
      description = "Delete a team page. Requires admin permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Page deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or page not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deletePage(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Page URL slug") @PathParam("pageSlug") String pageSlug) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    teamPageService.deletePage(team, pageSlug, user);
    return Response.noContent().build();
  }

  @PUT
  @Path("/reorder")
  @RolesAllowed("user")
  @Operation(
      summary = "Reorder pages",
      description = "Reorder team pages. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Pages reordered successfully",
        content = @Content(schema = @Schema(implementation = TeamPageSummaryDto[].class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid page IDs",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response reorderPages(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid ReorderPagesRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    List<TeamPageSummaryDto> pages = teamPageService.reorderPages(team, request, user);
    return Response.ok(pages).build();
  }

  @PATCH
  @Path("/{pageSlug}/slug")
  @RolesAllowed("user")
  @Operation(
      operationId = "changePageSlug",
      summary = "Change page slug",
      description = "Change page URL slug. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = TeamPageDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Invalid slug format",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team admin",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or page not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Current page URL slug") @PathParam("pageSlug") String currentSlug,
      @Valid SlugChangeRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);
    TeamPageDto page = teamPageService.updateSlug(team, currentSlug, request.slug(), user);
    return Response.ok(page).build();
  }
}
