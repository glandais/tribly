package com.tribly.api.rides;

import com.tribly.common.TsidUtils;
import com.tribly.dto.common.request.SlugChangeRequest;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.rides.request.*;
import com.tribly.dto.rides.response.*;
import com.tribly.service.ride.RideService;
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

@Path("/api/teams/{teamSlug}/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Rides", description = "Ride management and participation operations")
public class RideResource {

  @Inject RideService rideService;

  @POST
  @Operation(summary = "Create ride", description = "Create a new ride with optional groups")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Ride created successfully",
        content = @Content(schema = @Schema(implementation = RideDto.class))),
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
  public Response createRide(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Valid RideRequest request) {

    RideDto ride = rideService.createRide(teamSlug, request);

    return Response.status(Response.Status.CREATED).entity(ride).build();
  }

  @GET
  @Path("/{rideSlug}")
  @PermitAll
  @Operation(
      summary = "Get ride details",
      description = "Get detailed ride information including groups")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ride retrieved successfully",
        content = @Content(schema = @Schema(implementation = RideDto.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ride not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response getRide(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {

    RideDto ride = rideService.getDto(teamSlug, rideSlug);
    return Response.ok(ride).build();
  }

  @PUT
  @Path("/{rideSlug}")
  @Transactional
  @Operation(
      summary = "Update ride",
      description = "Update ride information. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Ride updated successfully",
        content = @Content(schema = @Schema(implementation = RideDto.class))),
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
        description = "User is not authorized to update this ride",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ride not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response updateRide(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Valid RideRequest request) {

    RideDto updatedRide = rideService.updateRide(teamSlug, rideSlug, request);

    return Response.ok(updatedRide).build();
  }

  @DELETE
  @Path("/{rideSlug}")
  @Operation(
      summary = "Delete ride",
      description = "Soft delete a ride. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Ride deleted successfully"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "403",
        description = "User is not authorized to delete this ride",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ride not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response deleteRide(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {

    rideService.deleteRide(teamSlug, rideSlug);
    return Response.noContent().build();
  }

  @PATCH
  @Path("/{rideSlug}/slug")
  @Operation(
      operationId = "changeRideSlug",
      summary = "Change ride slug",
      description = "Change ride URL slug. Requires organizer permissions.")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Slug changed successfully",
        content = @Content(schema = @Schema(implementation = RideDto.class))),
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
        description = "User is not authorized to change this ride's slug",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team or ride not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "409",
        description = "Slug already in use",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response changeSlug(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Current ride URL slug") @PathParam("rideSlug") String currentSlug,
      @Valid SlugChangeRequest request) {

    RideDto ride = rideService.updateSlug(teamSlug, currentSlug, request.slug());
    return Response.ok(ride).build();
  }

  @POST
  @Path("/{rideSlug}/groups/{groupId}/join")
  @Operation(summary = "Join ride group", description = "Join a ride group")
  @APIResponses({
    @APIResponse(
        responseCode = "201",
        description = "Successfully joined group",
        content = @Content(schema = @Schema(implementation = RideParticipationDto.class))),
    @APIResponse(
        responseCode = "400",
        description = "Group is full or user already joined",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team, ride, or group not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response joinGroup(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

    RideParticipationDto participation =
        rideService.joinGroup(teamSlug, rideSlug, TsidUtils.toLong(groupId));

    return Response.status(Response.Status.CREATED).entity(participation).build();
  }

  @POST
  @Path("/{rideSlug}/groups/{groupId}/leave")
  @Operation(summary = "Leave ride group", description = "Leave a ride group")
  @APIResponses({
    @APIResponse(responseCode = "204", description = "Successfully left group"),
    @APIResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team, ride, group, or participation not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @RolesAllowed("user")
  public Response leaveGroup(
      @Parameter(description = "Team URL slug") @PathParam("teamSlug") String teamSlug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

    rideService.leaveGroup(teamSlug, rideSlug, TsidUtils.toLong(groupId));

    return Response.noContent().build();
  }
}
