package com.tribly.api.ads;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.ads.request.AdRequest;
import com.tribly.dto.ads.response.AdDto;
import com.tribly.dto.ads.response.AdListResponse;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.enums.AdType;
import com.tribly.service.ad.AdService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.time.Instant;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jspecify.annotations.Nullable;

@Path("/api/teams/{slug}/ads")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Ads", description = "Ad management operations")
public class AdResource extends AbstractAuthenticatedResource {

  @Inject AdService adService;

  @GET
  @PermitAll
  @Operation(
      summary = "List ads",
      description = "Get paginated list of ads for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ads retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listAds(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Search by name/description") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Filter by ad type") @QueryParam("adType") @Nullable AdType adType,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    User user = getCurrentUserOrNull();

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    Team team = teamService.getTeam(slug);
    AdListResponse ads = adService.listAds(team, user, search, adType, from, to, page, size);

    return Response.ok(ads).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(
      summary = "Create ad",
      description = "Create a new ad. Any team member can create ads.")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Ad created successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
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
  public Response createAd(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid AdRequest request) {
    User user = getCurrentUser();

    Team team = teamService.getTeam(slug);
    AdDto ad = adService.createAd(team, request, user);

    return Response.created(URI.create("/api/teams/" + ad.team().slug() + "/ads/" + ad.slug()))
        .entity(ad)
        .build();
  }

  @GET
  @Path("/{adSlug}")
  @PermitAll
  @Operation(summary = "Get ad details", description = "Get detailed ad information")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad retrieved successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getAd(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Ad URL slug") @PathParam("adSlug") String adSlug) {
    User user = getCurrentUserOrNull();
    Team team = teamService.getTeam(teamSlug);
    AdDto ad = adService.getAdDetail(team, adSlug, user);
    return Response.ok(ad).build();
  }

  @PUT
  @Path("/{adSlug}")
  @Transactional
  @RolesAllowed("user")
  @Operation(
      summary = "Update ad",
      description = "Update ad information. Only the creator or an admin can update.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ad updated successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
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
        description = "User is not authorized to update this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateAd(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ad URL slug") @PathParam("adSlug") String adSlug,
      @Valid AdRequest request) {

    User user = getCurrentUser();

    Team team = teamService.getTeam(slug);
    AdDto updatedAd = adService.updateAd(team, adSlug, request, user);

    return Response.ok(updatedAd).build();
  }

  @DELETE
  @Path("/{adSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Delete ad",
      description = "Soft delete an ad. Only the creator or an admin can delete.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Ad deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this ad",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteAd(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ad URL slug") @PathParam("adSlug") String adSlug) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    adService.deleteAd(team, adSlug, user);
    return Response.noContent().build();
  }

  @PATCH
  @Path("/{adSlug}/slug")
  @RolesAllowed("user")
  @Operation(
      operationId = "changeAdSlug",
      summary = "Change ad slug",
      description = "Change ad URL slug. Requires admin permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = AdDto.class))),
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
        description = "User is not authorized to change this ad's slug",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ad not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Current ad URL slug") @PathParam("adSlug") String currentSlug,
      @Valid SlugChangeRequest request) {
    User user = getCurrentUser();
    Team team = teamService.getTeam(teamSlug);
    AdDto ad = adService.updateSlug(team, currentSlug, request.slug(), user);
    return Response.ok(ad).build();
  }
}
