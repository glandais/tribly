package com.tribly.api.rides;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.dto.error.ErrorResponse;
import com.tribly.dto.rides.request.*;
import com.tribly.dto.rides.response.*;
import com.tribly.enums.RideStatus;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.ride.RideService;
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

@Path("/api/teams/{slug}/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Rides", description = "Ride management and participation operations")
public class RideResource extends AbstractAuthenticatedResource {

  @Inject RideService rideService;

  @GET
  @PermitAll
  @Operation(
      summary = "List rides",
      description = "Get paginated list of rides for a team with optional filtering")
  @APIResponses({
    @APIResponse(
        responseCode = "200",
        description = "Rides retrieved successfully",
        content = @Content(schema = @Schema(implementation = RideListResponse.class))),
    @APIResponse(
        responseCode = "404",
        description = "Team not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  public Response listRides(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Start date filter (ISO format)") @QueryParam("from")
          @Nullable String fromStr,
      @Parameter(description = "End date filter (ISO format)") @QueryParam("to")
          @Nullable String toStr,
      @Parameter(description = "Status filter") @QueryParam("status") @Nullable RideStatus status,
      @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
      @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

    Long userId = getCurrentUserIdOrNull();

    LocalDateTime from = fromStr != null ? LocalDateTime.parse(fromStr) : null;
    LocalDateTime to = toStr != null ? LocalDateTime.parse(toStr) : null;

    RideListResponse rides = rideService.listRides(slug, userId, from, to, status, page, size);

    return Response.ok(rides).build();
  }

  @POST
  @RolesAllowed("user")
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
  public Response createRide(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Valid RideRequest request) {
    Long userId = getCurrentUserId();

    RideDto ride = rideService.createRide(slug, request, userId);

    return Response.created(URI.create("/api/teams/" + slug + "/rides/" + ride.slug()))
        .entity(ride)
        .build();
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
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
    Long userId = getCurrentUserIdOrNull();
    RideDto ride = rideService.getRideDetail(slug, rideSlug, userId);
    return Response.ok(ride).build();
  }

  @PUT
  @Path("/{rideSlug}")
  @Transactional
  @RolesAllowed("user")
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
  public Response updateRide(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Valid RideRequest request) {

    Long userId = getCurrentUserId();

    RideDto updatedRide = rideService.updateRide(slug, rideSlug, request, userId);

    return Response.ok(updatedRide).build();
  }

  @DELETE
  @Path("/{rideSlug}")
  @RolesAllowed("user")
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
  public Response deleteRide(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
    Long userId = getCurrentUserId();
    rideService.deleteRide(slug, rideSlug, userId);
    return Response.noContent().build();
  }

  @POST
  @Path("/{rideSlug}/groups/{groupId}/join")
  @RolesAllowed("user")
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
  public Response joinGroup(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

    Long userId = getCurrentUserId();

    RideParticipationDto participation =
        rideService.joinGroup(slug, rideSlug, TsidUtils.toLong(groupId), userId);

    return Response.created(
            URI.create(
                "/api/teams/"
                    + slug
                    + "/rides/"
                    + rideSlug
                    + "/groups/"
                    + groupId
                    + "/participants/"
                    + participation.id()))
        .entity(participation)
        .build();
  }

  @POST
  @Path("/{rideSlug}/groups/{groupId}/leave")
  @RolesAllowed("user")
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
  public Response leaveGroup(
      @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
      @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
      @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

    Long userId = getCurrentUserId();

    rideService.leaveGroup(slug, rideSlug, TsidUtils.toLong(groupId), userId);

    return Response.noContent().build();
  }
}
