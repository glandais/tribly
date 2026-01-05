package com.tribly.api.trips;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripListResponse;
import com.tribly.dto.trips.response.TripParticipationDto;
import com.tribly.infrastructure.exception.NewSlugException;
import com.tribly.service.trip.TripService;
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

@Path("/api/teams/{slug}/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Trips", description = "Trip management and participation operations")
public class TripResource extends AbstractAuthenticatedResource {

  @Inject TripService tripService;

  @GET
  @PermitAll
  @Operation(
      summary = "List trips",
      description = "Get paginated list of trips for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Trips retrieved successfully",
        content = @Content(schema = @Schema(implementation = TripListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listTrips(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Search by name/markdown") @QueryParam("search")
          @Nullable String search,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();

    Instant from = fromStr != null ? Instant.parse(fromStr) : null;
    Instant to = toStr != null ? Instant.parse(toStr) : null;

    TripListResponse trips = tripService.listTrips(slug, userId, search, from, to, page, size);

    return Response.ok(trips).build();
  }

  @POST
  @RolesAllowed("user")
  @Operation(summary = "Create trip", description = "Create a new trip with optional stages")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Trip created successfully",
        content = @Content(schema = @Schema(implementation = TripDto.class))),
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
  public Response createTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid TripRequest request) {
    Long userId = getCurrentUserId();

    TripDto trip = tripService.createTrip(slug, request, userId);

    return Response.created(URI.create("/api/teams/" + slug + "/trips/" + trip.getSlug()))
        .entity(trip)
        .build();
  }

  @GET
  @Path("/{tripSlug}")
  @PermitAll
  @Operation(
      summary = "Get trip details",
      description = "Get detailed trip information including stages and participants")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Trip retrieved successfully",
        content = @Content(schema = @Schema(implementation = TripDto.class))),
    @APIResponse(responseCode = "302", description = "Slug has changed, redirect to new URL"),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {
    Long userId = getCurrentUserIdOrNull();
    try {
      TripDto trip = tripService.getTripDetail(teamSlug, tripSlug, userId);
      return Response.ok(trip).build();
    } catch (NewSlugException e) {
      String newUrl = "/api/teams/" + e.getTeamSlug() + "/trips/" + e.getNewSlug();
      return Response.status(302).header("Location", newUrl).build();
    }
  }

  @PUT
  @Path("/{tripSlug}")
  @Transactional
  @RolesAllowed("user")
  @Operation(
      summary = "Update trip",
      description = "Update trip information. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Trip updated successfully",
        content = @Content(schema = @Schema(implementation = TripDto.class))),
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
        description = "User is not authorized to update this trip",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response updateTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug,
      @Valid TripRequest request) {

    Long userId = getCurrentUserId();

    TripDto updatedTrip = tripService.updateTrip(slug, tripSlug, request, userId);

    return Response.ok(updatedTrip).build();
  }

  @DELETE
  @Path("/{tripSlug}")
  @RolesAllowed("user")
  @Operation(
      summary = "Delete trip",
      description = "Soft delete a trip. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Trip deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this trip",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response deleteTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {
    Long userId = getCurrentUserId();
    tripService.deleteTrip(slug, tripSlug, userId);
    return Response.noContent().build();
  }

  @POST
  @Path("/{tripSlug}/join")
  @RolesAllowed("user")
  @Operation(summary = "Join trip", description = "Join a trip as a participant")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Successfully joined trip",
        content = @Content(schema = @Schema(implementation = TripParticipationDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "User already joined or trip not published",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response joinTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    Long userId = getCurrentUserId();

    TripParticipationDto participation = tripService.joinTrip(slug, tripSlug, userId);

    return Response.created(
            URI.create(
                "/api/teams/"
                    + slug
                    + "/trips/"
                    + tripSlug
                    + "/participants/"
                    + participation.id()))
        .entity(participation)
        .build();
  }

  @POST
  @Path("/{tripSlug}/leave")
  @RolesAllowed("user")
  @Operation(summary = "Leave trip", description = "Leave a trip as a participant")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Successfully left trip"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team, trip, or participation not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response leaveTrip(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    Long userId = getCurrentUserId();

    tripService.leaveTrip(slug, tripSlug, userId);

    return Response.noContent().build();
  }

  @PATCH
  @Path("/{tripSlug}/slug")
  @RolesAllowed("user")
  @Operation(
      operationId = "changeTripSlug",
      summary = "Change trip slug",
      description = "Change trip URL slug. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = TripDto.class))),
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
        description = "User is not authorized to change this trip's slug",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("slug") String teamSlug,
      @Parameter(description = "Current trip URL slug") @PathParam("tripSlug") String currentSlug,
      @Valid SlugChangeRequest request) {
    Long userId = getCurrentUserId();
    TripDto trip = tripService.updateSlug(teamSlug, currentSlug, request.slug(), userId);
    return Response.ok(trip).build();
  }
}
