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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

        // Validate visibility: private teams can only have team-only rides
        Visibility visibility = request.visibility() != null ? request.visibility() : Visibility.TEAM;
        if (!team.isPublic() && visibility == Visibility.PUBLIC) {
            throw BusinessException.validation("Private teams can only have team-only rides");
        }

        // Generate slug from title, ensure unique within team
        String slug = generateSlug(request.title());
        if (rideRepository.existsByTeamAndSlug(teamId, slug)) {
            slug = slug + "-" + System.currentTimeMillis() % 10000;
        }

        Ride ride = new Ride(team, creator, request.title(), slug, request.date());
        ride.setDescription(request.description());
        ride.setStartTime(request.startTime());
        ride.setVisibility(visibility);
        ride.setStatus(RideStatus.DRAFT);
        ride.setPublishAt(request.publishAt());

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
        var membership = securityService.requireMembership(userId, teamId);

        Optional<Ride> ride = rideRepository.findByIdAndTeam(rideId, teamId);

        // Only admins and organizers can see draft rides
        if (ride.isPresent() && ride.get().getStatus() == RideStatus.DRAFT && !membership.isOrganizer()) {
            return Optional.empty();
        }

        return ride;
    }

    public List<Ride> listRides(Long teamId, Long userId, LocalDate from, LocalDate to, RideStatus status, int page, int size) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        var membershipOpt = securityService.getMembership(userId, teamId);
        boolean isMember = membershipOpt.isPresent();
        boolean canSeeDrafts = isMember && membershipOpt.get().isOrganizer();

        List<Ride> rides;
        if (from != null && to != null) {
            rides = rideRepository.findByTeamAndDateRange(teamId, from, to, canSeeDrafts, page, size);
        } else if (status != null) {
            rides = rideRepository.findByTeamAndStatus(teamId, status, page, size);
        } else {
            rides = rideRepository.findByTeam(teamId, canSeeDrafts, page, size);
        }

        // For non-members of public teams, filter to show only public rides
        if (!isMember && team.isPublic()) {
            rides = rides.stream()
                    .filter(ride -> ride.getVisibility() == Visibility.PUBLIC)
                    .toList();
        } else if (!isMember) {
            // Private team - no access for non-members
            throw BusinessException.forbidden("You are not a member of this team");
        }

        return rides;
    }

    public long countRides(Long teamId, Long userId) {
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        var membershipOpt = securityService.getMembership(userId, teamId);
        boolean isMember = membershipOpt.isPresent();
        boolean canSeeDrafts = isMember && membershipOpt.get().isOrganizer();

        // Private team - no access for non-members
        if (!isMember && !team.isPublic()) {
            throw BusinessException.forbidden("You are not a member of this team");
        }

        // Get base count
        long count = rideRepository.countByTeam(teamId, canSeeDrafts);

        // For non-members of public teams, we need to count only public rides
        // Note: This is an approximation since the repository method doesn't filter by visibility
        // For accurate counts, we would need to add a repository method that filters by visibility
        if (!isMember && team.isPublic()) {
            // Count only public, non-draft rides for non-members
            return rideRepository.findByTeam(teamId, false, 0, Integer.MAX_VALUE).stream()
                    .filter(ride -> ride.getVisibility() == Visibility.PUBLIC)
                    .count();
        }

        return count;
    }

    @Transactional
    public Ride updateRide(Long teamId, Long rideId, UpdateRideRequest request, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to edit
        securityService.requireCanEditRide(userId, teamId, ride);

        // Validate visibility: private teams can only have team-only rides
        if (request.visibility() != null) {
            Team team = teamRepository.findActiveById(teamId)
                    .orElseThrow(() -> BusinessException.notFound("Team", teamId));
            if (!team.isPublic() && request.visibility() == Visibility.PUBLIC) {
                throw BusinessException.validation("Private teams can only have team-only rides");
            }
            ride.setVisibility(request.visibility());
        }

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
        // publishAt can be explicitly set to null to remove scheduled publishing
        ride.setPublishAt(request.publishAt());

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
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        var membershipOpt = securityService.getMembership(userId, teamId);
        boolean isMember = membershipOpt.isPresent();

        // For non-members, only allow viewing groups of public rides from public teams
        if (!isMember) {
            if (!team.isPublic() || ride.getVisibility() != Visibility.PUBLIC || ride.getStatus() == RideStatus.DRAFT) {
                throw BusinessException.forbidden("You don't have permission to view this ride's groups");
            }
        }

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
        Team team = teamRepository.findActiveById(teamId)
                .orElseThrow(() -> BusinessException.notFound("Team", teamId));

        var membershipOpt = securityService.getMembership(userId, teamId);
        boolean isMember = membershipOpt.isPresent();

        Optional<Ride> rideOpt = rideRepository.findByTeamAndSlug(teamId, rideSlug);

        if (rideOpt.isEmpty()) {
            return Optional.empty();
        }

        Ride ride = rideOpt.get();

        // Draft rides are only visible to admins and organizers
        if (ride.getStatus() == RideStatus.DRAFT) {
            if (!isMember || !membershipOpt.get().isOrganizer()) {
                return Optional.empty();
            }
        }

        // For non-members, only allow viewing public rides from public teams
        if (!isMember) {
            if (!team.isPublic() || ride.getVisibility() != Visibility.PUBLIC) {
                throw BusinessException.forbidden("You don't have permission to view this ride");
            }
        }

        return Optional.of(ride);
    }

    @Transactional
    public RideGroup updateGroup(Long teamId, Long rideId, Long groupId, UpdateRideGroupRequest request, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to edit groups
        securityService.requireCanEditRide(userId, teamId, ride);

        RideGroup group = rideGroupRepository.findByIdAndRide(groupId, rideId)
                .orElseThrow(() -> BusinessException.notFound("Group", groupId));

        if (request.name() != null) {
            group.setName(request.name());
        }
        if (request.description() != null) {
            group.setDescription(request.description());
        }
        if (request.averageSpeed() != null) {
            group.setAverageSpeed(request.averageSpeed());
        }
        if (request.maxParticipants() != null) {
            group.setMaxParticipants(request.maxParticipants());
        }

        rideGroupRepository.persist(group);
        LOG.infov("Group {0} updated in ride {1} by user {2}", groupId, rideId, userId);
        return group;
    }

    @Transactional
    public void deleteGroup(Long teamId, Long rideId, Long groupId, Long userId) {
        Ride ride = rideRepository.findByIdAndTeam(rideId, teamId)
                .orElseThrow(() -> BusinessException.notFound("Ride", rideId));

        // Security check: must be admin or creator (if organizer) to delete groups
        securityService.requireCanEditRide(userId, teamId, ride);

        RideGroup group = rideGroupRepository.findByIdAndRide(groupId, rideId)
                .orElseThrow(() -> BusinessException.notFound("Group", groupId));

        // Soft delete the group
        group.setDeleted(true);
        rideGroupRepository.persist(group);

        LOG.infov("Group {0} deleted from ride {1} by user {2}", groupId, rideId, userId);
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
            Instant publishAt,
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
            Long meetingPointId,
            Instant publishAt
    ) {}

    public record CreateRideGroupRequest(
            String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            Long routeId
    ) {}

    public record UpdateRideGroupRequest(
            String name,
            String description,
            Integer averageSpeed,
            Integer maxParticipants,
            Long routeId
    ) {}
}
