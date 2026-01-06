package com.tribly.api.places;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.service.place.PlaceService;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
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

@Path("/api/teams/{slug}/places")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Places", description = "Place management for ride starting/ending points")
public class PlaceResource extends AbstractAuthenticatedResource {

  @Inject PlaceService placeService;

  @GET
  @RolesAllowed("user")
  @Operation(summary = "List places", description = "Get all places for a team")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Places retrieved successfully",
        content = @Content(schema = @Schema(implementation = PlaceListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listPlaces(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("50") int size) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);
    PlaceListResponse places = placeService.listPlaces(team, page, size, user);
    return Response.ok(places).build();
  }

  @GET
  @RolesAllowed("user")
  @Path("/{placeId}")
  @Operation(summary = "Get place details", description = "Get a specific place by ID")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Place retrieved successfully",
        content = @Content(schema = @Schema(implementation = PlaceDetailDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or place not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getPlace(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Place ID (TSID)") @PathParam("placeId") String placeId) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);

    PlaceDetailDto place = placeService.getPlace(team, placeId, user);
    return Response.ok(place).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(summary = "Create place", description = "Create a new place for the team")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Place created successfully",
        content = @Content(schema = @Schema(implementation = PlaceDetailDto.class))),
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
        description = "User is not a team organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response createPlace(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid PlaceRequest request) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);

    PlaceDetailDto place = placeService.createPlace(team, request, user);
    return Response.status(Response.Status.CREATED).entity(place).build();
  }

  @PUT
  @Path("/{placeId}")
  @RolesAllowed("user")
  @Operation(summary = "Update place", description = "Update an existing place")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Place updated successfully",
        content = @Content(schema = @Schema(implementation = PlaceDetailDto.class))),
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
        description = "User is not a team organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or place not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updatePlace(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Place ID (TSID)") @PathParam("placeId") String placeId,
      @Valid PlaceRequest request) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);

    PlaceDetailDto place = placeService.updatePlace(team, placeId, request, user);
    return Response.ok(place).build();
  }

  @DELETE
  @Path("/{placeId}")
  @RolesAllowed("user")
  @Operation(summary = "Delete place", description = "Soft delete a place")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Place deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not a team organizer",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or place not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deletePlace(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Place ID (TSID)") @PathParam("placeId") String placeId) {

    User user = getCurrentUser();
    Team team = teamService.getTeam(slug);

    placeService.deletePlace(team, placeId, user);
    return Response.noContent().build();
  }
}
