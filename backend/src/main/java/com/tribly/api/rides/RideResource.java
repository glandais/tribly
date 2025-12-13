package com.tribly.api.rides;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.*;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.ride.RideService;
import io.quarkus.security.identity.SecurityIdentity;
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
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Path("/v1/teams/{teamId}/rides")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed("user")
public class RideResource {

    @Inject
    RideService rideService;

    @Inject
    SecurityIdentity securityIdentity;

    private Long getCurrentUserId() {
        return securityIdentity.getAttribute("userId");
    }

    @GET
    public Response listRides(
            @PathParam("teamId") Long teamId,
            @QueryParam("from") String fromStr,
            @QueryParam("to") String toStr,
            @QueryParam("status") RideStatus status,
            @QueryParam("page") @DefaultValue("0") int page,
            @QueryParam("size") @DefaultValue("20") int size) {

        Long userId = getCurrentUserId();
        if (!rideService.isMember(userId, teamId)) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        LocalDate from = fromStr != null ? LocalDate.parse(fromStr) : null;
        LocalDate to = toStr != null ? LocalDate.parse(toStr) : null;

        List<Ride> rides = rideService.listRides(teamId, from, to, status, page, size);
        long total = rideService.countRides(teamId);

        List<RideDto> dtos = rides.stream().map(RideDto::from).toList();
        return Response.ok(new RideListResponse(dtos, total, page, size)).build();
    }

    @POST
    public Response createRide(@PathParam("teamId") Long teamId, @Valid CreateRideRequest request) {
        Long userId = getCurrentUserId();

        List<RideService.CreateRideGroupRequest> groupRequests = null;
        if (request.groups() != null) {
            groupRequests = request.groups().stream()
                    .map(g -> new RideService.CreateRideGroupRequest(
                            g.name(), g.description(), g.averageSpeed(), g.maxParticipants(), g.routeId()))
                    .toList();
        }

        Ride ride = rideService.createRide(teamId,
                new RideService.CreateRideRequest(
                        request.title(),
                        request.description(),
                        request.date(),
                        request.startTime(),
                        request.visibility(),
                        request.routeId(),
                        request.meetingPointId(),
                        groupRequests
                ),
                userId
        );

        return Response.created(URI.create("/v1/teams/" + teamId + "/rides/" + ride.getId()))
                .entity(RideDto.from(ride))
                .build();
    }

    @GET
    @Path("/{rideId}")
    public Response getRide(@PathParam("teamId") Long teamId, @PathParam("rideId") Long rideId) {
        Long userId = getCurrentUserId();
        if (!rideService.isMember(userId, teamId)) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        Ride ride = rideService.getRide(teamId, rideId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        return Response.ok(RideDetailDto.from(ride)).build();
    }

    @PATCH
    @Path("/{rideId}")
    @Transactional
    public Response updateRide(
            @PathParam("teamId") Long teamId,
            @PathParam("rideId") Long rideId,
            @Valid UpdateRideRequest request) {

        Long userId = getCurrentUserId();

        Ride ride = rideService.updateRide(teamId, rideId,
                new RideService.UpdateRideRequest(
                        request.title(),
                        request.description(),
                        request.date(),
                        request.startTime(),
                        request.status(),
                        request.visibility(),
                        request.routeId(),
                        request.meetingPointId()
                ),
                userId
        );

        return Response.ok(RideDto.from(ride)).build();
    }

    @DELETE
    @Path("/{rideId}")
    public Response deleteRide(@PathParam("teamId") Long teamId, @PathParam("rideId") Long rideId) {
        Long userId = getCurrentUserId();
        rideService.deleteRide(teamId, rideId, userId);
        return Response.noContent().build();
    }

    @GET
    @Path("/{rideId}/groups")
    public Response listGroups(@PathParam("teamId") Long teamId, @PathParam("rideId") Long rideId) {
        Long userId = getCurrentUserId();
        if (!rideService.isMember(userId, teamId)) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        List<RideGroup> groups = rideService.listGroups(teamId, rideId);
        List<RideGroupDto> dtos = groups.stream().map(RideGroupDto::from).toList();
        return Response.ok(new RideGroupListResponse(dtos)).build();
    }

    @POST
    @Path("/{rideId}/groups")
    public Response createGroup(
            @PathParam("teamId") Long teamId,
            @PathParam("rideId") Long rideId,
            @Valid CreateGroupRequest request) {

        Long userId = getCurrentUserId();

        RideGroup group = rideService.createGroup(teamId, rideId,
                new RideService.CreateRideGroupRequest(
                        request.name(),
                        request.description(),
                        request.averageSpeed(),
                        request.maxParticipants(),
                        request.routeId()
                ),
                userId
        );

        return Response.created(URI.create("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + group.getId()))
                .entity(RideGroupDto.from(group))
                .build();
    }

    @POST
    @Path("/{rideId}/groups/{groupId}/join")
    public Response joinGroup(
            @PathParam("teamId") Long teamId,
            @PathParam("rideId") Long rideId,
            @PathParam("groupId") Long groupId,
            JoinGroupRequest request) {

        Long userId = getCurrentUserId();
        String notes = request != null ? request.notes() : null;

        RideParticipation participation = rideService.joinGroup(teamId, rideId, groupId, userId, notes);

        return Response.created(URI.create("/v1/teams/" + teamId + "/rides/" + rideId + "/groups/" + groupId + "/participants/" + participation.getId()))
                .entity(RideParticipationDto.from(participation))
                .build();
    }

    @POST
    @Path("/{rideId}/groups/{groupId}/leave")
    public Response leaveGroup(
            @PathParam("teamId") Long teamId,
            @PathParam("rideId") Long rideId,
            @PathParam("groupId") Long groupId) {

        Long userId = getCurrentUserId();
        rideService.leaveGroup(teamId, rideId, groupId, userId);

        return Response.noContent().build();
    }

    // Request DTOs
    public record CreateRideRequest(
            @NotBlank @Size(min = 3, max = 200) String title,
            @Size(max = 5000) String description,
            @NotNull LocalDate date,
            LocalTime startTime,
            Long routeId,
            Long meetingPointId,
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
            Long routeId,
            Long meetingPointId
    ) {}

    public record CreateGroupRequest(
            @NotBlank @Size(min = 1, max = 100) String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            Long routeId
    ) {}

    public record JoinGroupRequest(String notes) {}

    // Response DTOs
    public record RideDto(
            Long id,
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
                    ride.getId(),
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
            Long id,
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
                    ride.getId(),
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
            Long id,
            String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            int currentParticipants,
            int sortOrder
    ) {
        public static RideGroupDto from(RideGroup group) {
            return new RideGroupDto(
                    group.getId(),
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
            Long id,
            Long userId,
            String status,
            String registeredAt,
            String notes
    ) {
        public static RideParticipationDto from(RideParticipation participation) {
            return new RideParticipationDto(
                    participation.getId(),
                    participation.getUser().getId(),
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
}
