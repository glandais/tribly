package com.tribly.api.places;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.places.request.PlaceRequest;
import com.tribly.dto.places.response.PlaceDetailDto;
import com.tribly.dto.places.response.PlaceListResponse;
import com.tribly.service.place.PlaceService;
import jakarta.annotation.security.PermitAll;
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
  @PermitAll
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

    PlaceListResponse places = placeService.listPlaces(slug, page, size);
    return Response.ok(places).build();
  }

  @GET
  @Path("/{placeId}")
  @PermitAll
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

    Long userId = getCurrentUserIdOrNull();
    PlaceDetailDto place = placeService.getPlace(slug, placeId, userId);
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

    Long userId = getCurrentUserId();
    PlaceDetailDto place = placeService.createPlace(slug, request, userId);
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

    Long userId = getCurrentUserId();
    PlaceDetailDto place = placeService.updatePlace(slug, placeId, request, userId);
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

    Long userId = getCurrentUserId();
    placeService.deletePlace(slug, placeId, userId);
    return Response.noContent().build();
  }
}
