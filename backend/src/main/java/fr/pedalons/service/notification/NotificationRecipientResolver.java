package fr.pedalons.service.notification;

import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.NotificationSubjectType;
import fr.pedalons.enums.Status;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.service.notification.event.CommentReplied;
import fr.pedalons.service.notification.event.NotificationEvent;
import fr.pedalons.service.notification.event.PostPublished;
import fr.pedalons.service.notification.event.RideCancelled;
import fr.pedalons.service.notification.event.RidePublished;
import fr.pedalons.service.notification.event.TripCancelled;
import fr.pedalons.service.notification.event.TripPublished;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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

  /** What the notification points at, and who receives it. The actor is not yet removed. */
  public record Resolution(
      TeamEntity subject,
      NotificationSubjectType subjectType,
      @Nullable String excerpt,
      List<User> recipients) {}

  public Optional<Resolution> resolve(NotificationEvent event) {
    Instant now = Instant.now();
    return switch (event) {
      case RidePublished e ->
          live(rideRepository.findById(e.rideId()), Status.PUBLISHED, now)
              .map(ride -> toTeam(ride, NotificationSubjectType.RIDE));
      case RideCancelled e ->
          live(rideRepository.findById(e.rideId()), Status.CANCELLED, now)
              .map(
                  ride ->
                      new Resolution(
                          ride,
                          NotificationSubjectType.RIDE,
                          null,
                          rideParticipationRepository.findRegisteredMemberUsers(ride.getId())));
      case TripPublished e ->
          live(tripRepository.findById(e.tripId()), Status.PUBLISHED, now)
              .map(trip -> toTeam(trip, NotificationSubjectType.TRIP));
      case TripCancelled e ->
          live(tripRepository.findById(e.tripId()), Status.CANCELLED, now)
              .map(
                  trip ->
                      new Resolution(
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
    };
  }

  private Resolution toTeam(TeamEntity subject, NotificationSubjectType type) {
    return new Resolution(
        subject, type, null, userTeamRepository.findActiveMemberUsers(subject.getTeam().getId()));
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
    TeamEntity subject = reply.getTeamEntity();
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
        new Resolution(
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
