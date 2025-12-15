package com.tribly.service.ride;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.ride.*;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamRepository;
import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;

@ApplicationScoped
public class RideService {

    private static final Logger LOG = Logger.getLogger(RideService.class);
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    @Inject
    RideRepository rideRepository;

    @Inject
    RideGroupRepository rideGroupRepository;

    @Inject
    RideParticipationRepository participationRepository;

    @Inject
    TeamRepository teamRepository;

    @Inject
    UserRepository userRepository;

    @Inject
    TeamSecurityService securityService;

    @Transactional
    public Ride createRide(Long teamId, CreateRideRequest request, Long creatorId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        User creator = userRepository.findActiveById(creatorId)
                .orElseThrow(() -> BusinessException.notFound("User", creatorId));

        // Security check: must be admin or organizer to create rides
        securityService.requireCanCreateRide(creatorId, teamId);

        // Generate slug from title, ensure unique within team
        String slug = generateSlug(request.title());
        if (rideRepository.existsByTeamAndSlug(teamId, slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        Ride ride = new Ride(team, creator, request.title(), slug, request.date());
        ride.setDescription(request.description());
        ride.setStartTime(request.startTime());
        ride.setVisibility(request.visibility() != null ? request.visibility() : Visibility.TEAM);
        ride.setStatus(RideStatus.DRAFT);

        rideRepository.persist(ride);

        if (request.groups() != null && !request.groups().isEmpty()) {
            int sortOrder = 0;
            for (CreateRideGroupRequest groupRequest : request.groups()) {
                RideGroup group = new RideGroup(ride, groupRequest.name());
                group.setDescription(groupRequest.description());
                group.setAverageSpeed(groupRequest.averageSpeed());
                group.setMaxParticipants(groupRequest.maxParticipants());
                group.setSortOrder(sortOrder++);
                ride.addGroup(group);
                rideGroupRepository.persist(group);
            }
        } else {
            RideGroup defaultGroup = new RideGroup(ride, "Main Group");
            ride.addGroup(defaultGroup);
            rideGroupRepository.persist(defaultGroup);
        }

        LOG.infov("Ride '{0}' created by user {1} for team {2}", ride.getTitle(), creatorId, teamId);
        return ride;
    }

    public Optional<Ride> getRide(Long teamId, Long rideId, Long userId) {
        // Security check: must be a team member to view rides
        securityService.requireMembership(userId, teamId);
        return rideRepository.findByIdAndTeam(rideId, teamId);
    }

    public List<Ride> listRides(Long teamId, Long userId, LocalDate from, LocalDate to, RideStatus status, int page, int size) {
        // Security check: must be a team member to view rides
        securityService.requireMembership(userId, teamId);

        if (from != null && to != null) {
            return rideRepository.findByTeamAndDateRange(teamId, from, to, page, size);
        }
        if (status != null) {
            return rideRepository.findByTeamAndStatus(teamId, status, page, size);
        }
        return rideRepository.findByTeam(teamId, page, size);
    }

    public long countRides(Long teamId, Long userId) {
        // Security check: must be a team member to count rides
        securityService.requireMembership(userId, teamId);
        return rideRepository.countByTeam(teamId);
    }

    @Transactional
    public Ride updateRide(Long teamId, Long rideId, UpdateRideRequest request, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to edit
        securityService.requireCanEditRide(userId, teamId, ride);

        if (request.title() != null) {
            ride.setTitle(request.title());
        }
        if (request.description() != null) {
            ride.setDescription(request.description());
        }
        if (request.date() != null) {
            ride.setDate(request.date());
        }
        if (request.startTime() != null) {
            ride.setStartTime(request.startTime());
        }
        if (request.status() != null) {
            ride.setStatus(request.status());
        }
        if (request.visibility() != null) {
            ride.setVisibility(request.visibility());
        }

        rideRepository.persist(ride);
        LOG.infov("Ride {0} updated by user {1}", rideId, userId);
        return ride;
    }

    @Transactional
    public void deleteRide(Long teamId, Long rideId, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to delete
        securityService.requireCanEditRide(userId, teamId, ride);

        ride.softDelete();
        rideRepository.persist(ride);
        LOG.infov("Ride {0} deleted by user {1}", rideId, userId);
    }

    @Transactional
    public RideGroup createGroup(Long teamId, Long rideId, CreateRideGroupRequest request, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to add groups
        securityService.requireCanEditRide(userId, teamId, ride);

        int maxSortOrder = ride.getGroups().stream()
                .mapToInt(RideGroup::getSortOrder)
                .max()
                .orElse(-1);

        RideGroup group = new RideGroup(ride, request.name());
        group.setDescription(request.description());
        group.setAverageSpeed(request.averageSpeed());
        group.setMaxParticipants(request.maxParticipants());
        group.setSortOrder(maxSortOrder + 1);

        ride.addGroup(group);
        rideGroupRepository.persist(group);

        LOG.infov("Group '{0}' added to ride {1} by user {2}", group.getName(), rideId, userId);
        return group;
    }

    public List<RideGroup> listGroups(Long teamId, Long rideId, Long userId) {
        // Security check: must be a team member to view groups
        securityService.requireMembership(userId, teamId);

        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));
        return rideGroupRepository.findByRide(rideId);
    }

    @Transactional
    public RideParticipation joinGroup(Long teamId, Long rideId, Long groupId, Long userId, String notes) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        if (ride.getStatus() != RideStatus.PUBLISHED) {
            throw BusinessException.validation("Can only join published rides");
        }

        RideGroup group = rideGroupRepository.findByIdAndRide(groupId, rideId)
                .orElseThrow(() -> BusinessException.notFound("Group", groupId));

        User user = userRepository.findActiveById(userId)
                .orElseThrow(() -> BusinessException.notFound("User", userId));

        // Security check: must be a team member to join rides
        securityService.requireMembership(userId, teamId);

        Optional<RideParticipation> existingParticipation = participationRepository.findByUserAndRide(userId, rideId);
        if (existingParticipation.isPresent()) {
            throw BusinessException.conflict("You are already registered for this ride", "ALREADY_REGISTERED");
        }

        if (!group.hasCapacity()) {
            throw BusinessException.conflict("This group is full", "GROUP_FULL");
        }

        RideParticipation participation = new RideParticipation(group, user);
        participation.setNotes(notes);

        group.addParticipation(participation);
        participationRepository.persist(participation);

        LOG.infov("User {0} joined group {1} in ride {2}", userId, groupId, rideId);
        return participation;
    }

    @Transactional
    public void leaveGroup(Long teamId, Long rideId, Long groupId, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        RideGroup group = rideGroupRepository.findByIdAndRide(groupId, rideId)
                .orElseThrow(() -> BusinessException.notFound("Group", groupId));

        RideParticipation participation = participationRepository.findByUserAndGroup(userId, groupId)
                .orElseThrow(() -> BusinessException.notFound("You are not registered for this group"));

        participation.setStatus(ParticipationStatus.CANCELLED);
        participationRepository.persist(participation);

        LOG.infov("User {0} left group {1} in ride {2}", userId, groupId, rideId);
    }

    public Optional<Ride> getRideBySlug(Long teamId, String rideSlug, Long userId) {
        // Security check: must be a team member to view rides
        securityService.requireMembership(userId, teamId);
        return rideRepository.findByTeamAndSlug(teamId, rideSlug);
    }

    private String generateSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
    }

    public record CreateRideRequest(
            String title,
            String description,
            LocalDate date,
            LocalTime startTime,
            Visibility visibility,
            Long routeId,
            Long meetingPointId,
            List<CreateRideGroupRequest> groups
    ) {}

    public record UpdateRideRequest(
            String title,
            String description,
            LocalDate date,
            LocalTime startTime,
            RideStatus status,
            Visibility visibility,
            Long routeId,
            Long meetingPointId
    ) {}

    public record CreateRideGroupRequest(
            String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            Long routeId
    ) {}
}
