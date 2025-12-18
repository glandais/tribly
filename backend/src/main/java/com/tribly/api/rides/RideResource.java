package com.tribly.api.rides;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.ride.RideGroup;
import com.tribly.domain.ride.RideParticipation;
import com.tribly.domain.ride.RideStatus;
import com.tribly.domain.team.Team;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.ride.RideService;
import com.tribly.service.team.TeamService;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Path("/api/teams/{slug}/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Rides", description = "Ride management and participation operations")
public class RideResource extends AbstractAuthenticatedResource {

    @Inject
    RideService rideService;

    @Inject
    TeamService teamService;

    @GET
    @PermitAll
    @Operation(summary = "List rides", description = "Get paginated list of rides for a team with optional filtering")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Rides retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RideListResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response listRides(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Start date filter (ISO format)") @QueryParam("from") String fromStr,
            @Parameter(description = "End date filter (ISO format)") @QueryParam("to") String toStr,
            @Parameter(description = "Status filter") @QueryParam("status") RideStatus status,
            @Parameter(description = "Page number") @QueryParam("page") @DefaultValue("0") int page,
            @Parameter(description = "Page size") @QueryParam("size") @DefaultValue("20") int size) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserIdOrNull();

        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : null;
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : null;

        List<Ride> rides = rideService.listRides(team.getId(), userId, from, to, status, page, size);
        long total = rideService.countRides(team.getId(), userId);

        List<RideDto> dtos = rides.stream().map(RideDto::from).toList();
        return Response.ok(new RideListResponse(dtos, total, page, size)).build();
    }

    @POST
    @RolesAllowed("user")
    @Operation(summary = "Create ride", description = "Create a new ride with optional groups")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Ride created successfully",
                    content = @Content(schema = @Schema(implementation = RideDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not a team member"),
            @APIResponse(responseCode = "404", description = "Team not found")
    })
    public Response createRide(@Parameter(description = "Team URL slug") @PathParam("slug") String slug, @Valid CreateRideRequest request) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        List<RideService.CreateRideGroupRequest> groupRequests = null;
        if (request.groups() != null) {
            groupRequests = request.groups().stream()
                    .map(g -> new RideService.CreateRideGroupRequest(
                            g.name(), g.description(), g.averageSpeed(), g.maxParticipants(), TsidUtils.toLong(g.routeId())))
                    .toList();
        }

        Ride ride = rideService.createRide(team.getId(),
                new RideService.CreateRideRequest(
                        request.title(),
                        request.description(),
                        request.date(),
                        request.startTime(),
                        request.visibility(),
                        TsidUtils.toLong(request.routeId()),
                        TsidUtils.toLong(request.meetingPointId()),
                        request.publishAt(),
                        groupRequests
                ),
                userId
        );

        return Response.created(URI.create("/api/teams/" + slug + "/rides/" + ride.getSlug()))
                .entity(RideDto.from(ride))
                .build();
    }

    @GET
    @Path("/{rideSlug}")
    @PermitAll
    @Operation(summary = "Get ride details", description = "Get detailed ride information including groups")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Ride retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RideDetailDto.class))
            ),
            @APIResponse(responseCode = "404", description = "Team or ride not found")
    })
    public Response getRide(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserIdOrNull();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        return Response.ok(RideDetailDto.from(ride)).build();
    }

    @PATCH
    @Path("/{rideSlug}")
    @Transactional
    @RolesAllowed("user")
    @Operation(summary = "Update ride", description = "Update ride information. Requires organizer permissions.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Ride updated successfully",
                    content = @Content(schema = @Schema(implementation = RideDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not authorized to update this ride"),
            @APIResponse(responseCode = "404", description = "Team or ride not found")
    })
    public Response updateRide(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Valid UpdateRideRequest request) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        Ride updatedRide = rideService.updateRide(team.getId(), ride.getId(),
                new RideService.UpdateRideRequest(
                        request.title(),
                        request.description(),
                        request.date(),
                        request.startTime(),
                        request.status(),
                        request.visibility(),
                        TsidUtils.toLong(request.routeId()),
                        TsidUtils.toLong(request.meetingPointId()),
                        request.publishAt()
                ),
                userId
        );

        return Response.ok(RideDto.from(updatedRide)).build();
    }

    @DELETE
    @Path("/{rideSlug}")
    @RolesAllowed("user")
    @Operation(summary = "Delete ride", description = "Soft delete a ride. Requires organizer permissions.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Ride deleted successfully"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not authorized to delete this ride"),
            @APIResponse(responseCode = "404", description = "Team or ride not found")
    })
    public Response deleteRide(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.deleteRide(team.getId(), ride.getId(), userId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{rideSlug}/groups")
    @PermitAll
    @Operation(summary = "List ride groups", description = "Get all groups for a ride")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Groups retrieved successfully",
                    content = @Content(schema = @Schema(implementation = RideGroupListResponse.class))
            ),
            @APIResponse(responseCode = "404", description = "Team or ride not found")
    })
    public Response listGroups(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserIdOrNull();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        List<RideGroup> groups = rideService.listGroups(team.getId(), ride.getId(), userId);
        List<RideGroupDto> dtos = groups.stream().map(RideGroupDto::from).toList();
        return Response.ok(new RideGroupListResponse(dtos)).build();
    }

    @POST
    @Path("/{rideSlug}/groups")
    @RolesAllowed("user")
    @Operation(summary = "Create ride group", description = "Create a new group for a ride. Requires organizer permissions.")
    @APIResponses({
            @APIResponse(
                    responseCode = "201",
                    description = "Group created successfully",
                    content = @Content(schema = @Schema(implementation = RideGroupDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not authorized to modify this ride"),
            @APIResponse(responseCode = "404", description = "Team or ride not found")
    })
    public Response createGroup(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Valid CreateGroupRequest request) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        RideGroup group = rideService.createGroup(team.getId(), ride.getId(),
                new RideService.CreateRideGroupRequest(
                        request.name(),
                        request.description(),
                        request.averageSpeed(),
                        request.maxParticipants(),
                        TsidUtils.toLong(request.routeId())
                ),
                userId
        );

        return Response.created(URI.create("/api/teams/" + slug + "/rides/" + rideSlug + "/groups/" + TsidUtils.toString(group.getId())))
                .entity(RideGroupDto.from(group))
                .build();
    }

    @PATCH
    @Path("/{rideSlug}/groups/{groupId}")
    @RolesAllowed("user")
    @Operation(summary = "Update ride group", description = "Update a ride group. Requires organizer permissions.")
    @APIResponses({
            @APIResponse(
                    responseCode = "200",
                    description = "Group updated successfully",
                    content = @Content(schema = @Schema(implementation = RideGroupDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Invalid request"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not authorized to modify this ride"),
            @APIResponse(responseCode = "404", description = "Team, ride, or group not found")
    })
    public Response updateGroup(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId,
            @Valid UpdateGroupRequest request) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        RideGroup group = rideService.updateGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId),
                new RideService.UpdateRideGroupRequest(
                        request.name(),
                        request.description(),
                        request.averageSpeed(),
                        request.maxParticipants(),
                        TsidUtils.toLong(request.routeId())
                ),
                userId
        );

        return Response.ok(RideGroupDto.from(group)).build();
    }

    @DELETE
    @Path("/{rideSlug}/groups/{groupId}")
    @RolesAllowed("user")
    @Operation(summary = "Delete ride group", description = "Delete a ride group. Requires organizer permissions.")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Group deleted successfully"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "403", description = "User is not authorized to modify this ride"),
            @APIResponse(responseCode = "404", description = "Team, ride, or group not found")
    })
    public Response deleteGroup(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.deleteGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId);

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
                    content = @Content(schema = @Schema(implementation = RideParticipationDto.class))
            ),
            @APIResponse(responseCode = "400", description = "Group is full or user already joined"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "Team, ride, or group not found")
    })
    public Response joinGroup(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId,
            JoinGroupRequest request) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();
        String notes = request != null ? request.notes() : null;

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        RideParticipation participation = rideService.joinGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId, notes);

        return Response.created(URI.create("/api/teams/" + slug + "/rides/" + rideSlug + "/groups/" + groupId + "/participants/" + TsidUtils.toString(participation.getId())))
                .entity(RideParticipationDto.from(participation))
                .build();
    }

    @POST
    @Path("/{rideSlug}/groups/{groupId}/leave")
    @RolesAllowed("user")
    @Operation(summary = "Leave ride group", description = "Leave a ride group")
    @APIResponses({
            @APIResponse(responseCode = "204", description = "Successfully left group"),
            @APIResponse(responseCode = "401", description = "Unauthorized"),
            @APIResponse(responseCode = "404", description = "Team, ride, group, or participation not found")
    })
    public Response leaveGroup(
            @Parameter(description = "Team URL slug") @PathParam("slug") String slug,
            @Parameter(description = "Ride URL slug") @PathParam("rideSlug") String rideSlug,
            @Parameter(description = "Group ID (TSID)") @PathParam("groupId") String groupId) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.leaveGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId);

        return Response.noContent().build();
    }

    // Request DTOs
    @Schema(description = "Ride creation request")
    public record CreateRideRequest(
            @Schema(description = "Ride title", examples = "Sunday Morning Ride", required = true)
            @NotBlank @Size(min = 3, max = 200) String title,

            @Schema(description = "Ride description")
            @Size(max = 5000) String description,

            @Schema(description = "Ride date", examples = "2025-06-15", required = true)
            @NotNull LocalDate date,

            @Schema(description = "Start time", examples = "09:00")
            LocalTime startTime,

            @Schema(description = "Route ID (TSID)")
            String routeId,

            @Schema(description = "Meeting point ID (TSID)")
            String meetingPointId,

            @Schema(description = "Visibility level", enumeration = {"PUBLIC", "MEMBERS_ONLY", "PRIVATE"})
            Visibility visibility,

            @Schema(description = "Publication timestamp (for scheduled publishing)")
            Instant publishAt,

            @Schema(description = "Ride groups to create")
            List<CreateGroupRequest> groups
    ) {
    }

    @Schema(description = "Ride update request")
    public record UpdateRideRequest(
            @Schema(description = "Ride title")
            @Size(min = 3, max = 200) String title,

            @Schema(description = "Ride description")
            @Size(max = 5000) String description,

            @Schema(description = "Ride date")
            LocalDate date,

            @Schema(description = "Start time")
            LocalTime startTime,

            @Schema(description = "Ride status", enumeration = {"DRAFT", "PUBLISHED", "CANCELLED", "COMPLETED"})
            RideStatus status,

            @Schema(description = "Visibility level", enumeration = {"PUBLIC", "MEMBERS_ONLY", "PRIVATE"})
            Visibility visibility,

            @Schema(description = "Route ID (TSID)")
            String routeId,

            @Schema(description = "Meeting point ID (TSID)")
            String meetingPointId,

            @Schema(description = "Publication timestamp")
            Instant publishAt
    ) {
    }

    @Schema(description = "Ride group creation request")
    public record CreateGroupRequest(
            @Schema(description = "Group name", examples = "Fast Group", required = true)
            @NotBlank @Size(min = 1, max = 100) String name,

            @Schema(description = "Group description")
            String description,

            @Schema(description = "Average speed in km/h", examples = "25")
            Integer averageSpeed,

            @Schema(description = "Maximum participants")
            Integer maxParticipants,

            @Schema(description = "Route ID (TSID) for this group")
            String routeId
    ) {
    }

    @Schema(description = "Ride group update request")
    public record UpdateGroupRequest(
            @Schema(description = "Group name")
            @Size(min = 1, max = 100) String name,

            @Schema(description = "Group description")
            String description,

            @Schema(description = "Average speed in km/h")
            Integer averageSpeed,

            @Schema(description = "Maximum participants")
            Integer maxParticipants,

            @Schema(description = "Route ID (TSID)")
            String routeId
    ) {
    }

    @Schema(description = "Request to join a ride group")
    public record JoinGroupRequest(
            @Schema(description = "Optional notes for the organizer")
            String notes
    ) {
    }

    // Response DTOs
    @Schema(description = "Ride summary data")
    public record RideDto(
            @Schema(description = "Ride ID (TSID)")
            String id,

            @Schema(description = "Ride URL slug")
            String slug,

            @Schema(description = "Ride title")
            String title,

            @Schema(description = "Ride description")
            String description,

            @Schema(description = "Ride date")
            LocalDate date,

            @Schema(description = "Start time")
            LocalTime startTime,

            @Schema(description = "Ride status", enumeration = {"DRAFT", "PUBLISHED", "CANCELLED", "COMPLETED"})
            String status,

            @Schema(description = "Visibility level", enumeration = {"PUBLIC", "MEMBERS_ONLY", "PRIVATE"})
            String visibility,

            @Schema(description = "Number of participants")
            int participantCount,

            @Schema(description = "Number of groups")
            int groupCount,

            @Schema(description = "Publication timestamp")
            String publishAt,

            @Schema(description = "Creation timestamp")
            String createdAt
    ) {
        public static RideDto from(Ride ride) {
            return new RideDto(
                    TsidUtils.toString(ride.getId()),
                    ride.getSlug(),
                    ride.getTitle(),
                    ride.getDescription(),
                    ride.getDate(),
                    ride.getStartTime(),
                    ride.getStatus().name(),
                    ride.getVisibility().name(),
                    ride.getParticipantCount(),
                    ride.getGroupCount(),
                    ride.getPublishAt() != null ? ride.getPublishAt().toString() : null,
                    ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null
            );
        }
    }

    @Schema(description = "Detailed ride information")
    public record RideDetailDto(
            @Schema(description = "Ride ID (TSID)")
            String id,

            @Schema(description = "Ride URL slug")
            String slug,

            @Schema(description = "Ride title")
            String title,

            @Schema(description = "Ride description")
            String description,

            @Schema(description = "Ride date")
            LocalDate date,

            @Schema(description = "Start time")
            LocalTime startTime,

            @Schema(description = "Ride status")
            String status,

            @Schema(description = "Visibility level")
            String visibility,

            @Schema(description = "Number of participants")
            int participantCount,

            @Schema(description = "Number of groups")
            int groupCount,

            @Schema(description = "Ride groups")
            List<RideGroupDto> groups,

            @Schema(description = "Publication timestamp")
            String publishAt,

            @Schema(description = "Creation timestamp")
            String createdAt
    ) {
        public static RideDetailDto from(Ride ride) {
            List<RideGroupDto> groupDtos = ride.getGroups().stream()
                    .filter(g -> !g.isDeleted())
                    .map(RideGroupDto::from)
                    .toList();

            return new RideDetailDto(
                    TsidUtils.toString(ride.getId()),
                    ride.getSlug(),
                    ride.getTitle(),
                    ride.getDescription(),
                    ride.getDate(),
                    ride.getStartTime(),
                    ride.getStatus().name(),
                    ride.getVisibility().name(),
                    ride.getParticipantCount(),
                    ride.getGroupCount(),
                    groupDtos,
                    ride.getPublishAt() != null ? ride.getPublishAt().toString() : null,
                    ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null
            );
        }
    }

    @Schema(description = "Ride group information")
    public record RideGroupDto(
            @Schema(description = "Group ID (TSID)")
            String id,

            @Schema(description = "Group name")
            String name,

            @Schema(description = "Group description")
            String description,

            @Schema(description = "Average speed in km/h")
            Integer averageSpeed,

            @Schema(description = "Maximum participants")
            Integer maxParticipants,

            @Schema(description = "Current number of participants")
            int currentParticipants,

            @Schema(description = "Sort order")
            int sortOrder
    ) {
        public static RideGroupDto from(RideGroup group) {
            return new RideGroupDto(
                    TsidUtils.toString(group.getId()),
                    group.getName(),
                    group.getDescription(),
                    group.getAverageSpeed(),
                    group.getMaxParticipants(),
                    group.getCurrentParticipants(),
                    group.getSortOrder()
            );
        }
    }

    @Schema(description = "Ride participation information")
    public record RideParticipationDto(
            @Schema(description = "Participation ID (TSID)")
            String id,

            @Schema(description = "User ID (TSID)")
            String userId,

            @Schema(description = "Participation status")
            String status,

            @Schema(description = "Registration timestamp")
            String registeredAt,

            @Schema(description = "Participant notes")
            String notes
    ) {
        public static RideParticipationDto from(RideParticipation participation) {
            return new RideParticipationDto(
                    TsidUtils.toString(participation.getId()),
                    TsidUtils.toString(participation.getUser().getId()),
                    participation.getStatus().name(),
                    participation.getRegisteredAt() != null ? participation.getRegisteredAt().toString() : null,
                    participation.getNotes()
            );
        }
    }

    @Schema(description = "Paginated ride list response")
    public record RideListResponse(
            @Schema(description = "List of rides")
            List<RideDto> rides,

            @Schema(description = "Total number of rides")
            long total,

            @Schema(description = "Current page number")
            int page,

            @Schema(description = "Page size")
            int size
    ) {
    }

    @Schema(description = "Ride group list response")
    public record RideGroupListResponse(
            @Schema(description = "List of ride groups")
            List<RideGroupDto> data
    ) {
    }

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }
}
