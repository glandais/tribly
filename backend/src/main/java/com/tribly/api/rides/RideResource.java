package com.tribly.api.rides;

import com.tribly.api.AbstractAuthenticatedResource;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.*;
import com.tribly.domain.team.Team;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.ride.RideService;
import com.tribly.service.team.TeamService;
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

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Path("/v1/teams/{slug}/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class RideResource extends AbstractAuthenticatedResource {

    @Inject
    RideService rideService;

    @Inject
    TeamService teamService;

    @GET
    public Response listRides(
            @PathParam("slug") String slug,
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr,
            @QueryParam("status") RideStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : null;
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : null;

        List<Ride> rides = rideService.listRides(team.getId(), userId, from, to, status, page, size);
        long total = rideService.countRides(team.getId(), userId);

        List<RideDto> dtos = rides.stream().map(RideDto::from).toList();
        return Response.ok(new RideListResponse(dtos, total, page, size)).build();
    }

    @POST
    public Response createRide(@PathParam("slug") String slug, @Valid CreateRideRequest request) {
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
                        groupRequests
                ),
                userId
        );

        return Response.created(URI.create("/v1/teams/" + slug + "/rides/" + ride.getSlug()))
                .entity(RideDto.from(ride))
                .build();
    }

    @GET
    @Path("/{rideSlug}")
    public Response getRide(@PathParam("slug") String slug, @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        return Response.ok(RideDetailDto.from(ride)).build();
    }

    @PATCH
    @Path("/{rideSlug}")
    @Transactional
    public Response updateRide(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
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
                        TsidUtils.toLong(request.meetingPointId())
                ),
                userId
        );

        return Response.ok(RideDto.from(updatedRide)).build();
    }

    @DELETE
    @Path("/{rideSlug}")
    public Response deleteRide(@PathParam("slug") String slug, @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.deleteRide(team.getId(), ride.getId(), userId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{rideSlug}/groups")
    public Response listGroups(@PathParam("slug") String slug, @PathParam("rideSlug") String rideSlug) {
        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        List<RideGroup> groups = rideService.listGroups(team.getId(), ride.getId(), userId);
        List<RideGroupDto> dtos = groups.stream().map(RideGroupDto::from).toList();
        return Response.ok(new RideGroupListResponse(dtos)).build();
    }

    @POST
    @Path("/{rideSlug}/groups")
    public Response createGroup(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
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

        return Response.created(URI.create("/v1/teams/" + slug + "/rides/" + rideSlug + "/groups/" + TsidUtils.toString(group.getId())))
                .entity(RideGroupDto.from(group))
                .build();
    }

    @PATCH
    @Path("/{rideSlug}/groups/{groupId}")
    public Response updateGroup(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
            @PathParam("groupId") String groupId,
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
    public Response deleteGroup(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
            @PathParam("groupId") String groupId) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.deleteGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId);

        return Response.noContent().build();
    }

    @POST
    @Path("/{rideSlug}/groups/{groupId}/join")
    public Response joinGroup(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
            @PathParam("groupId") String groupId,
            JoinGroupRequest request) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();
        String notes = request != null ? request.notes() : null;

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        RideParticipation participation = rideService.joinGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId, notes);

        return Response.created(URI.create("/v1/teams/" + slug + "/rides/" + rideSlug + "/groups/" + groupId + "/participants/" + TsidUtils.toString(participation.getId())))
                .entity(RideParticipationDto.from(participation))
                .build();
    }

    @POST
    @Path("/{rideSlug}/groups/{groupId}/leave")
    public Response leaveGroup(
            @PathParam("slug") String slug,
            @PathParam("rideSlug") String rideSlug,
            @PathParam("groupId") String groupId) {

        Team team = getTeamBySlug(slug);
        Long userId = getCurrentUserId();

        Ride ride = rideService.getRideBySlug(team.getId(), rideSlug, userId)
                .orElseThrow(() -> BusinessException.notFound("Ride with slug '" + rideSlug + "' not found"));

        rideService.leaveGroup(team.getId(), ride.getId(), TsidUtils.toLong(groupId), userId);

        return Response.noContent().build();
    }

    // Request DTOs
    public record CreateRideRequest(
            @NotBlank @Size(min = 3, max = 200) String title,
            @Size(max = 5000) String description,
            @NotNull LocalDate date,
            LocalTime startTime,
            String routeId,
            String meetingPointId,
            Visibility visibility,
            List<CreateGroupRequest> groups
    ) {}

    public record UpdateRideRequest(
            @Size(min = 3, max = 200) String title,
            @Size(max = 5000) String description,
            LocalDate date,
            LocalTime startTime,
            RideStatus status,
            Visibility visibility,
            String routeId,
            String meetingPointId
    ) {}

    public record CreateGroupRequest(
            @NotBlank @Size(min = 1, max = 100) String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            String routeId
    ) {}

    public record UpdateGroupRequest(
            @Size(min = 1, max = 100) String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            String routeId
    ) {}

    public record JoinGroupRequest(String notes) {}

    // Response DTOs
    public record RideDto(
            String id,
            String slug,
            String title,
            String description,
            LocalDate date,
            LocalTime startTime,
            String status,
            String visibility,
            int participantCount,
            int groupCount,
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
                    ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null
            );
        }
    }

    public record RideDetailDto(
            String id,
            String slug,
            String title,
            String description,
            LocalDate date,
            LocalTime startTime,
            String status,
            String visibility,
            int participantCount,
            int groupCount,
            List<RideGroupDto> groups,
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
                    ride.getCreatedAt() != null ? ride.getCreatedAt().toString() : null
            );
        }
    }

    public record RideGroupDto(
            String id,
            String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            int currentParticipants,
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

    public record RideParticipationDto(
            String id,
            String userId,
            String status,
            String registeredAt,
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

    public record RideListResponse(
            List<RideDto> rides,
            long total,
            int page,
            int size
    ) {}

    public record RideGroupListResponse(List<RideGroupDto> data) {}

    private Team getTeamBySlug(String slug) {
        return teamService.getTeamBySlug(slug)
                .orElseThrow(() -> BusinessException.notFound("Team with slug '" + slug + "' not found"));
    }
}
