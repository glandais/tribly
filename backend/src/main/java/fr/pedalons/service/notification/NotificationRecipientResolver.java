package fr.pedalons.service.notification;

import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.TeamInvitation;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.NotificationChange;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.team.TeamInvitationRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.notification.event.CommentOnPublication;
import fr.pedalons.service.notification.event.CommentReplied;
import fr.pedalons.service.notification.event.NotificationEvent;
import fr.pedalons.service.notification.event.PostPublished;
import fr.pedalons.service.notification.event.RideCancelled;
import fr.pedalons.service.notification.event.RideJoined;
import fr.pedalons.service.notification.event.RidePublished;
import fr.pedalons.service.notification.event.RideReminder;
import fr.pedalons.service.notification.event.RideUpdated;
import fr.pedalons.service.notification.event.TeamInvited;
import fr.pedalons.service.notification.event.TripCancelled;
import fr.pedalons.service.notification.event.TripPublished;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.jspecify.annotations.Nullable;

/**
 * Decides, for one event, what it is about and who hears of it — or that it no longer matters.
 *
 * <p>Reloads every entity rather than trusting the payload: between the business commit and this
 * tick a ride may have been unpublished, deleted, or its date may have passed. Each of those is an
 * {@link Optional#empty()}, which the dispatcher records as {@code SKIPPED}.
 *
 * <p>The switch below has no {@code default}: {@link NotificationEvent} is sealed, so a new event
 * record does not compile until it is handled here.
 */
@ApplicationScoped
public class NotificationRecipientResolver {

  /** Longest quote kept in the snapshot; the column holds 500. */
  static final int EXCERPT_LENGTH = 280;

  @Inject RideRepository rideRepository;
  @Inject TripRepository tripRepository;
  @Inject PostRepository postRepository;
  @Inject CommentRepository commentRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject RideParticipationRepository rideParticipationRepository;
  @Inject TripParticipationRepository tripParticipationRepository;
  @Inject TeamInvitationRepository invitationRepository;
  @Inject UserRepository userRepository;

  /**
   * What the notification points at, and who receives it. The actor is not yet removed.
   *
   * <p>The subject is described by value rather than by entity: an invitation is about a team,
   * which is not a {@link TeamEntity}.
   */
  public record Resolution(
      Team team,
      NotificationSubjectType subjectType,
      String subjectSlug,
      String subjectName,
      @Nullable Instant subjectDateTime,
      @Nullable String excerpt,
      List<NotificationChange> changes,
      List<User> recipients) {

    static Resolution of(
        TeamEntity subject,
        NotificationSubjectType type,
        @Nullable String excerpt,
        List<User> recipients) {
      return new Resolution(
          subject.getTeam(),
          type,
          subject.getSlug(),
          subject.getName(),
          subject.getDateTime(),
          excerpt,
          List.of(),
          recipients);
    }
  }

  public Optional<Resolution> resolve(NotificationEvent event) {
    Instant now = Instant.now();
    return switch (event) {
      case RidePublished e ->
          live(rideRepository.findById(e.rideId()), Status.PUBLISHED, now)
              .map(ride -> toTeam(ride, NotificationSubjectType.RIDE));
      case RideCancelled e ->
          live(rideRepository.findById(e.rideId()), Status.CANCELLED, now).map(this::toRegistered);
      case TripPublished e ->
          live(tripRepository.findById(e.tripId()), Status.PUBLISHED, now)
              .map(trip -> toTeam(trip, NotificationSubjectType.TRIP));
      case TripCancelled e ->
          live(tripRepository.findById(e.tripId()), Status.CANCELLED, now)
              .map(
                  trip ->
                      Resolution.of(
                          trip,
                          NotificationSubjectType.TRIP,
                          null,
                          tripParticipationRepository.findRegisteredMemberUsers(trip.getId())));
      // A post has no date to have passed: its dateTime is when it was published.
      case PostPublished e ->
          Optional.ofNullable(postRepository.findById(e.postId()))
              .filter(post -> !post.isDeleted() && post.getStatus() == Status.PUBLISHED)
              .map(post -> toTeam(post, NotificationSubjectType.POST));
      case CommentReplied e -> resolveReply(commentRepository.findById(e.commentId()));
      // A ride moved since the reminder was queued has another reminder of its own coming.
      case RideReminder e ->
          live(rideRepository.findById(e.rideId()), Status.PUBLISHED, now)
              .filter(ride -> ride.getDateTime().equals(e.dateTime()))
              .map(this::toRegistered);
      case RideUpdated e ->
          live(rideRepository.findById(e.rideId()), Status.PUBLISHED, now)
              .flatMap(ride -> resolveUpdate(ride, e));
      case RideJoined e ->
          resolveJoin(rideParticipationRepository.findById(e.participationId()), now);
      case CommentOnPublication e ->
          resolveCommentOnPublication(commentRepository.findById(e.commentId()));
      case TeamInvited e -> resolveInvitation(invitationRepository.findById(e.invitationId()));
    };
  }

  private Resolution toTeam(TeamEntity subject, NotificationSubjectType type) {
    return Resolution.of(
        subject, type, null, userTeamRepository.findActiveMemberUsers(subject.getTeam().getId()));
  }

  private Resolution toRegistered(Ride ride) {
    return Resolution.of(
        ride,
        NotificationSubjectType.RIDE,
        null,
        rideParticipationRepository.findRegisteredMemberUsers(ride.getId()));
  }

  /**
   * The net effect of the edits since the event was queued. None — an edit undone, or a change of
   * name only — notifies nobody.
   */
  private Optional<Resolution> resolveUpdate(Ride ride, RideUpdated event) {
    List<NotificationChange> changes = new ArrayList<>();
    if (!ride.getDateTime().equals(event.previousDateTime())) {
      changes.add(NotificationChange.DATE_TIME);
    }
    Long startPlaceId = ride.getStart() != null ? ride.getStart().getId() : null;
    if (!Objects.equals(startPlaceId, event.previousStartPlaceId())) {
      changes.add(NotificationChange.START_PLACE);
    }
    if (changes.isEmpty()) {
      return Optional.empty();
    }
    Resolution registered = toRegistered(ride);
    return Optional.of(
        new Resolution(
            registered.team(),
            registered.subjectType(),
            registered.subjectSlug(),
            registered.subjectName(),
            registered.subjectDateTime(),
            null,
            List.copyOf(changes),
            registered.recipients()));
  }

  /**
   * The ride's creator and the leader of the group joined — the two people for whom a new rider
   * changes something. Registrations are hard-deleted: a rider who left before this tick is not
   * found. The group's name travels as the excerpt.
   */
  private Optional<Resolution> resolveJoin(@Nullable RideParticipation participation, Instant now) {
    if (participation == null) {
      return Optional.empty();
    }
    RideGroup group = participation.getRideGroup();
    return live(group.getRide(), Status.PUBLISHED, now)
        .map(
            ride -> {
              List<User> organisers = new ArrayList<>();
              organisers.add(ride.getCreatedBy());
              if (group.getLeader() != null) {
                organisers.add(group.getLeader());
              }
              return Resolution.of(
                  ride,
                  NotificationSubjectType.RIDE,
                  excerpt(group.getName()),
                  stillMembers(organisers, ride.getTeam()));
            });
  }

  /**
   * The author of what was commented on. Same rules as a reply: the thread must not be on a draft,
   * and the author must still belong to the team.
   */
  private Optional<Resolution> resolveCommentOnPublication(@Nullable Comment comment) {
    if (comment == null || comment.getParent() != null) {
      return Optional.empty();
    }
    TeamEntity subject = Hibernate.unproxy(comment.getTeamEntity(), TeamEntity.class);
    NotificationSubjectType type = subjectType(subject);
    if (type == null || subject.isDeleted() || subject.getStatus() == Status.DRAFT) {
      return Optional.empty();
    }
    return Optional.of(
        Resolution.of(
            subject,
            type,
            excerpt(comment.getContent()),
            stillMembers(List.of(subject.getCreatedBy()), subject.getTeam())));
  }

  /**
   * The account the invited address belongs to on the team's domain, if there is one. The only type
   * whose recipient is, by definition, not a member — so the membership guard is inverted: someone
   * who joined in the meantime has nothing left to accept.
   */
  private Optional<Resolution> resolveInvitation(@Nullable TeamInvitation invitation) {
    if (invitation == null || !invitation.isPending() || invitation.isLapsed()) {
      return Optional.empty();
    }
    Team team = invitation.getTeam();
    if (team.isDeleted()) {
      return Optional.empty();
    }
    List<User> recipients =
        userRepository
            .findByEmailAndDomain(
                invitation.getDomainId(), invitation.getEmail().toLowerCase(Locale.ROOT))
            // Unverified, the account may be someone else's claim on the address: telling it who
            // invited whom to which team would leak exactly what the invitation token protects.
            .filter(user -> !user.isDeleted() && user.isEmailVerified())
            .filter(
                user -> userTeamRepository.findByUserAndTeam(user.getId(), team.getId()).isEmpty())
            .map(List::of)
            .orElse(List.of());
    return Optional.of(
        new Resolution(
            team,
            NotificationSubjectType.TEAM,
            team.getSlug(),
            team.getName(),
            null,
            null,
            List.of(),
            recipients));
  }

  /** The live users of {@code candidates} who still belong to {@code team}. */
  private List<User> stillMembers(List<@Nullable User> candidates, Team team) {
    List<User> members = new ArrayList<>();
    for (User user : candidates) {
      if (user != null
          && !user.isDeleted()
          && userTeamRepository.findByUserAndTeam(user.getId(), team.getId()).isPresent()) {
        members.add(user);
      }
    }
    return members;
  }

  /**
   * A ride or trip still in the status the event announced, not deleted, and not in the past. The
   * date rule is what keeps a retroactive entry — or a mass import — from waking anyone up.
   */
  private static <T extends TeamEntity> Optional<T> live(
      @Nullable T entity, Status expected, Instant now) {
    return Optional.ofNullable(entity)
        .filter(e -> !e.isDeleted() && e.getStatus() == expected)
        .filter(e -> e.getDateTime().isAfter(now));
  }

  /**
   * The author of the parent comment, provided they are still a member of the team. Comments are
   * hard-deleted, so a reply removed before this tick is simply not found. A thread on a draft is
   * skipped: a draft is read by organisers only, and checking that per recipient buys little.
   */
  private Optional<Resolution> resolveReply(@Nullable Comment reply) {
    if (reply == null || reply.getParent() == null) {
      return Optional.empty();
    }
    // Unproxied: a lazy TeamEntity proxy matches none of the subtypes subjectType switches on.
    TeamEntity subject = Hibernate.unproxy(reply.getTeamEntity(), TeamEntity.class);
    NotificationSubjectType type = subjectType(subject);
    if (type == null || subject.isDeleted() || subject.getStatus() == Status.DRAFT) {
      return Optional.empty();
    }
    User parentAuthor = reply.getParent().getCreatedBy();
    boolean stillMember =
        !parentAuthor.isDeleted()
            && userTeamRepository
                .findByUserAndTeam(parentAuthor.getId(), subject.getTeam().getId())
                .isPresent();
    return Optional.of(
        Resolution.of(
            subject,
            type,
            excerpt(reply.getContent()),
            stillMember ? List.of(parentAuthor) : List.of()));
  }

  private static @Nullable NotificationSubjectType subjectType(TeamEntity entity) {
    return switch (entity) {
      case Ride ignored -> NotificationSubjectType.RIDE;
      case Trip ignored -> NotificationSubjectType.TRIP;
      case Post ignored -> NotificationSubjectType.POST;
      case Route ignored -> NotificationSubjectType.ROUTE;
      default -> null;
    };
  }

  static String excerpt(String content) {
    String flat = content.strip().replaceAll("\\s+", " ");
    return flat.length() <= EXCERPT_LENGTH
        ? flat
        : flat.substring(0, EXCERPT_LENGTH - 1).stripTrailing() + "…";
  }
}
