package fr.pedalons.service.comment;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentRequest;
import fr.pedalons.dto.comments.response.CommentDto;
import fr.pedalons.dto.comments.response.CommentListResponse;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.ActionType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.infrastructure.exception.NotFoundException;
import fr.pedalons.repository.comment.CommentRepository;
import fr.pedalons.service.post.PostService;
import fr.pedalons.service.ride.RideService;
import fr.pedalons.service.route.RouteService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.CheckAccess;
import fr.pedalons.service.team.TeamService;
import fr.pedalons.service.trip.TripService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentService {

  @Inject CommentRepository commentRepository;

  @Inject PedalonsQueryContext pedalonsQueryContext;
  @Inject TeamService teamService;
  @Inject PostService postService;
  @Inject RouteService routeService;
  @Inject RideService rideService;
  @Inject TripService tripService;

  TeamEntity getTeamEntity(Team team, String slug, EntityType entityType) {
    if (entityType == EntityType.POST) {
      return postService.findBySlug(team, slug);
    } else if (entityType == EntityType.RIDE) {
      return rideService.findBySlug(team, slug);
    } else if (entityType == EntityType.ROUTE) {
      return routeService.findBySlug(team, slug);
    } else if (entityType == EntityType.TRIP) {
      return tripService.findBySlug(team, slug);
    }
    throw new NotFoundException(entityType, slug);
  }

  @Transactional
  @CheckAccess(entityType = EntityType.COMMENT, action = ActionType.LIST)
  public CommentListResponse listComments(String teamSlug, String slug, EntityType entityType) {
    Team team = teamService.getTeam(teamSlug);
    TeamEntity teamEntity = getTeamEntity(team, slug, entityType);
    List<Comment> allComments = commentRepository.findByTeamEntityId(teamEntity.getId());

    // Organize into parent comments and replies
    Map<Long, List<Comment>> repliesByParentId =
        allComments.stream()
            .filter(c -> c.getParent() != null)
            .collect(Collectors.groupingBy(c -> c.getParent().getId()));

    List<CommentDto> dtos =
        allComments.stream()
            .filter(c -> c.getParent() == null)
            .map(
                parent -> {
                  List<CommentDto> replies =
                      repliesByParentId.getOrDefault(parent.getId(), List.of()).stream()
                          .map(reply -> CommentDto.from(reply, List.of()))
                          .toList();
                  return CommentDto.from(parent, replies);
                })
            .toList();

    return new CommentListResponse(dtos, allComments.size());
  }

  @Transactional
  @CheckAccess(entityType = EntityType.COMMENT, action = ActionType.CREATE)
  public CommentDto createComment(
      String teamSlug, String slug, EntityType entityType, CommentRequest request) {
    Team team = teamService.getTeam(teamSlug);
    TeamEntity teamEntity = getTeamEntity(team, slug, entityType);
    User creator = pedalonsQueryContext.getUser();

    Comment parent = null;
    if (request.parentId() != null) {
      Long parentId = TsidUtils.toLong(request.parentId());
      parent =
          commentRepository
              .findByTeamIdAndId(team.getId(), parentId)
              .orElseThrow(() -> new NotFoundException(EntityType.COMMENT, request.parentId()));

      // Enforce 1-level threading
      if (parent.getParent() != null) {
        throw new BusinessException(ErrorCode.UNKNOWN);
      }

      // Ensure parent belongs to same entity
      if (!parent.getTeamEntity().getId().equals(teamEntity.getId())) {
        throw new BusinessException(ErrorCode.UNKNOWN);
      }
    }

    Comment comment =
        parent != null
            ? new Comment(creator, teamEntity, parent, request.content())
            : new Comment(creator, teamEntity, request.content());

    commentRepository.persistAndFlush(comment);

    return CommentDto.from(comment, List.of());
  }

  @Transactional
  @CheckAccess(entityType = EntityType.COMMENT, action = ActionType.DELETE)
  // params used by CommentAccessChecker
  public void deleteComment(String teamSlug, String slug, EntityType entityType, Long commentId) {
    Team team = teamService.getTeam(teamSlug);
    Comment comment =
        commentRepository
            .findByTeamIdAndId(team.getId(), commentId)
            .orElseThrow(() -> new NotFoundException(EntityType.COMMENT, commentId));
    deleteRecursive(comment);
  }

  private void deleteRecursive(Comment comment) {
    commentRepository.findReplies(comment.getId()).forEach(this::deleteRecursive);
    commentRepository.delete(comment);
  }
}
