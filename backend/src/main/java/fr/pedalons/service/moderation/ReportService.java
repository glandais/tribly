package fr.pedalons.service.moderation;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.dto.moderation.request.ReportRequest;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.ReportTargetType;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.ad.AdRepository;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.moderation.ContentReportRepository;
import fr.pedalons.repository.post.PostRepository;
import fr.pedalons.repository.ride.RideRepository;
import fr.pedalons.repository.route.RouteRepository;
import fr.pedalons.repository.team.UserTeamRepository;
import fr.pedalons.repository.trip.TripRepository;
import fr.pedalons.service.notification.NotificationPublisher;
import fr.pedalons.service.notification.event.ContentReported;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.team.TeamService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import org.hibernate.Hibernate;
import org.jspecify.annotations.Nullable;

/**
 * Files a member's report (App Store guideline 1.2).
 *
 * <p>A report is always filed in a team, and only about something the reporter can read there:
 * anything else is a {@code 404}, exactly as if the target did not exist. The reporter stops seeing
 * the target in their lists at once (see {@code TeamEntityRepository} and {@code CommentService});
 * at {@value #AUTO_HIDE_REPORTERS} distinct reporters it is hidden from every member but the team's
 * moderators, until one of them decides (see {@link ModerationService}).
 */
@ApplicationScoped
public class ReportService {

  /** Distinct members with an open report after which the content is hidden from members. */
  public static final int AUTO_HIDE_REPORTERS = 3;

  /** Longest copy of the reported text kept for the moderator: {@code content_reports.excerpt}. */
  static final int EXCERPT_LENGTH = 1000;

  @Inject PedalonsQueryContext pedalonsContext;
  @Inject TeamService teamService;
  @Inject ContentReportRepository reportRepository;
  @Inject CommentRepository commentRepository;
  @Inject UserTeamRepository userTeamRepository;
  @Inject PostRepository postRepository;
  @Inject AdRepository adRepository;
  @Inject RideRepository rideRepository;
  @Inject TripRepository tripRepository;
  @Inject RouteRepository routeRepository;
  @Inject NotificationPublisher notificationPublisher;

  /** What a report is about, once proven readable by the reporter. */
  private record Target(
      User user,
      @Nullable String excerpt,
      @Nullable TeamEntity entity,
      @Nullable Comment comment) {}

  /**
   * Files a report. Idempotent: the same reporter reporting the same target again changes nothing.
   *
   * @throws NotFoundException the team, or the target, is not readable by the caller
   * @throws BadRequestException {@code REPORT_SELF} when the caller reports themselves or their own
   *     content
   */
  @Logged
  @Transactional
  public void report(ReportRequest request) {
    User reporter = pedalonsContext.getUser();
    Team team = teamService.getTeam(request.teamSlug());
    ReportTargetType type = request.targetType();
    Long targetId = TsidUtils.toLong(request.targetId());

    Target target = resolveTarget(team, reporter, type, targetId);
    if (target.user().getId().equals(reporter.getId())) {
      throw new BadRequestException(ErrorCode.REPORT_SELF);
    }
    // Idempotent, a double tap included: a second report by the same member is a no-op.
    Optional<Long> reportId =
        reportRepository.insertIfAbsent(
            pedalonsContext.getDomainId(),
            team.getId(),
            reporter.getId(),
            type,
            targetId,
            target.user().getId(),
            request.reason(),
            blankToNull(request.message()),
            excerpt(target.excerpt()),
            Instant.now());
    if (reportId.isEmpty()) {
      return;
    }

    hideIfReportedEnough(type, targetId, target);

    // No actor: the reporter is anonymous to the team.
    notificationPublisher.publish(
        new ContentReported(reportId.get(), team.getId(), type, targetId), team, null);
  }

  private Target resolveTarget(Team team, User reporter, ReportTargetType type, Long targetId) {
    boolean platformAdmin = reporter.isPlatformAdmin();
    return switch (type) {
      case MEMBER -> {
        requireMember(team, reporter, type, targetId);
        User member =
            userTeamRepository
                .findByUserAndTeam(targetId, team.getId())
                .map(UserTeam::getUser)
                .orElseThrow(() -> notFound(type, targetId));
        yield new Target(member, member.getDisplayName(), null, null);
      }
      case COMMENT -> {
        // Comments are a members-only surface: see CommentAccessChecker.
        requireMember(team, reporter, type, targetId);
        Comment comment =
            commentRepository
                .findByTeamIdAndId(team.getId(), targetId)
                .orElseThrow(() -> notFound(type, targetId));
        // The tombstone of an erased account has nothing left to report.
        if (comment.getCreatedBy().isDeleted()) {
          throw notFound(type, targetId);
        }
        TeamEntity parent = Hibernate.unproxy(comment.getTeamEntity(), TeamEntity.class);
        TeamEntityRepository<? extends TeamEntity, ?> repository = repositoryOf(parent);
        if (repository == null
            || repository
                .findByTeamAndId(
                    pedalonsContext.getDomainId(),
                    team.getId(),
                    reporter.getId(),
                    parent.getId(),
                    false,
                    platformAdmin)
                .isEmpty()) {
          throw notFound(type, targetId);
        }
        yield new Target(comment.getCreatedBy(), comment.getContent(), null, comment);
      }
      case POST, AD, RIDE, TRIP, ROUTE -> {
        if (type == ReportTargetType.AD) {
          // Ads are members-only: see AdAccessChecker.
          requireMember(team, reporter, type, targetId);
        }
        TeamEntity entity =
            repositoryOf(type)
                .findByTeamAndId(
                    pedalonsContext.getDomainId(),
                    team.getId(),
                    reporter.getId(),
                    targetId,
                    false,
                    platformAdmin)
                .orElseThrow(() -> notFound(type, targetId));
        String markdown = entity.getMarkdown().strip();
        String excerpt =
            markdown.isEmpty() ? entity.getName() : entity.getName() + " — " + markdown;
        yield new Target(entity.getCreatedBy(), excerpt, entity, null);
      }
    };
  }

  private void requireMember(Team team, User user, ReportTargetType type, Long targetId) {
    if (user.isPlatformAdmin()) {
      return;
    }
    if (userTeamRepository.findByUserAndTeam(user.getId(), team.getId()).isEmpty()) {
      throw notFound(type, targetId);
    }
  }

  /** At the threshold, hides the content from members until a moderator decides. */
  private void hideIfReportedEnough(ReportTargetType type, Long targetId, Target target) {
    if (type == ReportTargetType.MEMBER) {
      return;
    }
    if (reportRepository.countOpenReporters(pedalonsContext.getDomainId(), type, targetId)
        < AUTO_HIDE_REPORTERS) {
      return;
    }
    Instant now = Instant.now();
    TeamEntity entity = target.entity();
    if (entity != null && entity.getModerationHiddenAt() == null) {
      entity.setModerationHiddenAt(now);
    }
    Comment comment = target.comment();
    if (comment != null && comment.getModerationHiddenAt() == null) {
      comment.setModerationHiddenAt(now);
    }
  }

  private TeamEntityRepository<? extends TeamEntity, ?> repositoryOf(ReportTargetType type) {
    return switch (type) {
      case POST -> postRepository;
      case AD -> adRepository;
      case RIDE -> rideRepository;
      case TRIP -> tripRepository;
      case ROUTE -> routeRepository;
      case COMMENT, MEMBER -> throw new IllegalArgumentException("Not a publication: " + type);
    };
  }

  /** The repository of what a comment is on — null for anything that cannot carry comments. */
  private @Nullable TeamEntityRepository<? extends TeamEntity, ?> repositoryOf(TeamEntity entity) {
    return switch (entity) {
      case Post ignored -> postRepository;
      case Ride ignored -> rideRepository;
      case Trip ignored -> tripRepository;
      case Route ignored -> routeRepository;
      case Ad ignored -> adRepository;
      default -> null;
    };
  }

  private static NotFoundException notFound(ReportTargetType type, Long targetId) {
    EntityType entityType =
        switch (type) {
          case COMMENT -> EntityType.COMMENT;
          case POST -> EntityType.POST;
          case AD -> EntityType.AD;
          case RIDE -> EntityType.RIDE;
          case TRIP -> EntityType.TRIP;
          case ROUTE -> EntityType.ROUTE;
          case MEMBER -> EntityType.USER;
        };
    return new NotFoundException(entityType, targetId);
  }

  private static @Nullable String blankToNull(@Nullable String message) {
    if (message == null || message.isBlank()) {
      return null;
    }
    return message.strip();
  }

  /** The reported text, cut to what the column holds. */
  static @Nullable String excerpt(@Nullable String text) {
    if (text == null) {
      return null;
    }
    String stripped = text.strip();
    if (stripped.isEmpty()) {
      return null;
    }
    return stripped.length() <= EXCERPT_LENGTH
        ? stripped
        : stripped.substring(0, EXCERPT_LENGTH - 1).stripTrailing() + "…";
  }
}
