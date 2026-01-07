package com.tribly.api.trips;

import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.trips.request.TripRequest;
import com.tribly.dto.trips.response.TripDto;
import com.tribly.dto.trips.response.TripParticipationDto;
import com.tribly.service.trip.TripService;
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

@Path("/api/teams/{teamSlug}/trips")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Trips", description = "Trip management and participation operations")
public class TripResource {

  @Inject TripService tripService;

  @POST
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
  @RolesAllowed("user")
  public Response createTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid TripRequest request) {

    TripDto trip = tripService.createTrip(teamSlug, request);

    return Response.status(Response.Status.CREATED).entity(trip).build();
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
    @APIResponse(
        responseCode = "404",
        description = "Team or trip not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    TripDto trip = tripService.getDto(teamSlug, tripSlug);
    return Response.ok(trip).build();
  }

  @PUT
  @Path("/{tripSlug}")
  @Transactional
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
  @RolesAllowed("user")
  public Response updateTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug,
      @Valid TripRequest request) {

    TripDto updatedTrip = tripService.updateTrip(teamSlug, tripSlug, request);

    return Response.ok(updatedTrip).build();
  }

  @DELETE
  @Path("/{tripSlug}")
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
  @RolesAllowed("user")
  public Response deleteTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    tripService.deleteTrip(teamSlug, tripSlug);
    return Response.noContent().build();
  }

  @POST
  @Path("/{tripSlug}/join")
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
  @RolesAllowed("user")
  public Response joinTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    TripParticipationDto participation = tripService.joinTrip(teamSlug, tripSlug);

    return Response.status(Response.Status.CREATED).entity(participation).build();
  }

  @POST
  @Path("/{tripSlug}/leave")
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
  @RolesAllowed("user")
  public Response leaveTrip(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Trip URL slug") @PathParam("tripSlug") String tripSlug) {

    tripService.leaveTrip(teamSlug, tripSlug);

    return Response.noContent().build();
  }

  @PATCH
  @Path("/{tripSlug}/slug")
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
  @RolesAllowed("user")
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Current trip URL slug") @PathParam("tripSlug") String currentSlug,
      @Valid SlugChangeRequest request) {

    TripDto trip = tripService.updateSlug(teamSlug, currentSlug, request.slug());
    return Response.ok(trip).build();
  }
}
