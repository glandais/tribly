package com.tribly.service.ride;

import com.tribly.domain.common.TriblyPage;
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
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Pattern;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RideService {

  private static final Logger LOG = Logger.getLogger(RideService.class);
  private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

  @Inject RideRepository rideRepository;

  @Inject RideGroupRepository rideGroupRepository;

  @Inject RideParticipationRepository participationRepository;

  @Inject TeamRepository teamRepository;

  @Inject UserRepository userRepository;

  @Inject TeamSecurityService securityService;

  public TriblyPage<Ride> listRides(
      String slug,
      @Nullable Long userId,
      @Nullable LocalDate from,
      @Nullable LocalDate to,
      @Nullable RideStatus status,
      int page,
      int size) {
    Team team =
        teamRepository.findBySlug(slug).orElseThrow(() -> BusinessException.notFound("Team", slug));

    RideQueryParams rideQueryParams = getRideQueryParams(userId, status, team);

    RideQuery rideQuery =
        new RideQuery(
            team.getId(),
            page,
            size,
            null,
            from,
            to,
            rideQueryParams.visibility(),
            rideQueryParams.statuses());
    return rideRepository.find(rideQuery);
  }

  public Ride getRideBySlug(String teamSlug, String rideSlug, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    RideQueryParams rideQueryParams = getRideQueryParams(userId, null, team);

    RideQuery rideQuery =
        new RideQuery(
            team.getId(),
            0,
            1,
            rideSlug,
            null,
            null,
            rideQueryParams.visibility(),
            rideQueryParams.statuses());
    TriblyPage<Ride> triblyPage = rideRepository.find(rideQuery);
    if (triblyPage.items().isEmpty()) {
      throw BusinessException.notFound("Ride", rideSlug);
    } else {
      return triblyPage.items().getFirst();
    }
  }

  private record RideQueryParams(List<RideStatus> statuses, @Nullable Visibility visibility) {}

  private RideQueryParams getRideQueryParams(
      @Nullable Long userId, @Nullable RideStatus status, Team team) {
    boolean isMember = securityService.isMember(userId, team);
    boolean canSeeDrafts = securityService.canSeeDrafts(userId, team);
    List<RideStatus> statuses = getRideStatuses(status, canSeeDrafts);

    Visibility visibility = getVisibility(isMember, team);
    return new RideQueryParams(statuses, visibility);
  }

  private List<RideStatus> getRideStatuses(@Nullable RideStatus status, boolean canSeeDrafts) {
    List<RideStatus> statuses;
    if (status == null) {
      if (canSeeDrafts) {
        statuses = Arrays.asList(RideStatus.values());
      } else {
        statuses = List.of(RideStatus.PUBLISHED, RideStatus.CANCELLED);
      }
    } else {
      if (status.equals(RideStatus.DRAFT) && !canSeeDrafts) {
        statuses = List.of();
      } else {
        statuses = List.of(status);
      }
    }
    return statuses;
  }

  private @Nullable Visibility getVisibility(boolean isMember, Team team) {
    Visibility visibility = null;
    // For non-members of public teams, filter to show only public rides
    if (!isMember && team.getVisibility() == Visibility.PUBLIC) {
      visibility = Visibility.PUBLIC;
    } else if (!isMember) {
      // Private team - no access for non-members
      throw BusinessException.notFound("");
    }
    return visibility;
  }

  @Transactional
  public Ride createRide(String teamSlug, CreateRideRequest request, Long creatorId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(creatorId)
            .orElseThrow(() -> BusinessException.notFound("User", creatorId));

    // Security check: must be admin or organizer to create rides
    securityService.requireOrganizer(creatorId, team.getSlug());

    // Validate visibility: private teams can only have team-only rides
    Visibility visibility = request.visibility() != null ? request.visibility() : Visibility.TEAM;
    if (team.getVisibility() != Visibility.PUBLIC && visibility == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only rides");
    }

    // Generate slug from title, ensure unique within team
    String slug = generateSlug(request.title());
    if (rideRepository.existsByTeamAndSlug(team.getId(), slug)) {
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
    }

    LOG.infov("Ride '{0}' created by user {1} for team {2}", ride.getTitle(), creatorId, teamSlug);
    return ride;
  }

  @Transactional
  public Ride updateRide(String teamSlug, String rideSlug, UpdateRideRequest request, Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit
    securityService.requireOrganizer(userId, teamSlug);

    // Validate visibility: private teams can only have team-only rides
    if (request.visibility() != null) {
      Team team = ride.getTeam();
      if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
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
    LOG.infov("Ride {0} updated by user {1}", rideSlug, userId);
    return ride;
  }

  @Transactional
  public void deleteRide(String teamSlug, String rideSlug, Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to delete
    securityService.requireOrganizer(userId, teamSlug);

    ride.softDelete();
    rideRepository.persist(ride);
    LOG.infov("Ride {0} deleted by user {1}", rideSlug, userId);
  }

  @Transactional
  public RideGroup createGroup(
      String teamSlug, String rideSlug, CreateRideGroupRequest request, Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to add groups
    securityService.requireOrganizer(userId, teamSlug);

    int maxSortOrder = ride.getGroups().stream().mapToInt(RideGroup::getSortOrder).max().orElse(-1);

    RideGroup group = new RideGroup(ride, request.name());
    group.setDescription(request.description());
    group.setAverageSpeed(request.averageSpeed());
    group.setMaxParticipants(request.maxParticipants());
    group.setSortOrder(maxSortOrder + 1);

    ride.addGroup(group);
    rideGroupRepository.persist(group);

    LOG.infov("Group '{0}' added to ride {1} by user {2}", group.getName(), rideSlug, userId);
    return group;
  }

  public List<RideGroup> listGroups(String teamSlug, String rideSlug, @Nullable Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);
    return rideGroupRepository.findByRide(ride.getId());
  }

  @Transactional
  public RideParticipation joinGroup(
      String teamSlug, String rideSlug, Long groupId, Long userId, @Nullable String notes) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    if (ride.getStatus() != RideStatus.PUBLISHED) {
      throw BusinessException.validation("Can only join published rides");
    }

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> BusinessException.notFound("Group", groupId));

    User user =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Security check: must be a team member to join rides
    securityService.requireMembership(userId, teamSlug);

    Optional<RideParticipation> existingParticipation =
        participationRepository.findByUserAndRide(userId, ride.getId());
    if (existingParticipation.isPresent()) {
      throw BusinessException.conflict(
          "You are already registered for this ride", "ALREADY_REGISTERED");
    }

    if (!group.hasCapacity()) {
      throw BusinessException.conflict("This group is full", "GROUP_FULL");
    }

    RideParticipation participation = new RideParticipation(group, user);
    participation.setNotes(notes);

    group.addParticipation(participation);
    participationRepository.persist(participation);

    LOG.infov("User {0} joined group {1} in ride {2}", userId, groupId, ride.getId());
    return participation;
  }

  @Transactional
  public void leaveGroup(String teamSlug, String rideSlug, Long groupId, Long userId) {
    getRideBySlug(teamSlug, rideSlug, userId);

    RideParticipation participation =
        participationRepository
            .findByUserAndGroup(userId, groupId)
            .orElseThrow(() -> BusinessException.notFound("You are not registered for this group"));

    participation.setStatus(ParticipationStatus.CANCELLED);
    participationRepository.persist(participation);

    LOG.infov("User {0} left group {1} in ride {2}", userId, groupId, rideSlug);
  }

  @Transactional
  public RideGroup updateGroup(
      String teamSlug, String rideSlug, Long groupId, UpdateRideGroupRequest request, Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to edit groups
    securityService.requireOrganizer(userId, teamSlug);

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
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
    LOG.infov("Group {0} updated in ride {1} by user {2}", groupId, rideSlug, userId);
    return group;
  }

  @Transactional
  public void deleteGroup(String teamSlug, String rideSlug, Long groupId, Long userId) {
    Ride ride = getRideBySlug(teamSlug, rideSlug, userId);

    // Security check: must be admin or creator (if organizer) to delete groups
    securityService.requireOrganizer(userId, teamSlug);

    RideGroup group =
        rideGroupRepository
            .findByIdAndRide(groupId, ride.getId())
            .orElseThrow(() -> BusinessException.notFound("Group", groupId));

    // Soft delete the group
    group.setDeleted(true);
    rideGroupRepository.persist(group);

    LOG.infov("Group {0} deleted from ride {1} by user {2}", groupId, rideSlug, userId);
  }

  private String generateSlug(String input) {
    String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
    String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
    String slug = NONLATIN.matcher(normalized).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-").replaceAll("^-|-$", "");
  }

  public record CreateRideRequest(
      String title,
      @Nullable String description,
      LocalDate date,
      @Nullable LocalTime startTime,
      @Nullable Visibility visibility,
      @Nullable Long routeId,
      @Nullable Long meetingPointId,
      @Nullable Instant publishAt,
      @Nullable List<CreateRideGroupRequest> groups) {}

  public record UpdateRideRequest(
      @Nullable String title,
      @Nullable String description,
      @Nullable LocalDate date,
      @Nullable LocalTime startTime,
      @Nullable RideStatus status,
      @Nullable Visibility visibility,
      @Nullable Long routeId,
      @Nullable Long meetingPointId,
      @Nullable Instant publishAt) {}

  public record CreateRideGroupRequest(
      String name,
      @Nullable String description,
      @Nullable Integer averageSpeed,
      @Nullable Integer maxParticipants,
      @Nullable Long routeId) {}

  public record UpdateRideGroupRequest(
      @Nullable String name,
      @Nullable String description,
      @Nullable Integer averageSpeed,
      @Nullable Integer maxParticipants,
      @Nullable Long routeId) {}
}
