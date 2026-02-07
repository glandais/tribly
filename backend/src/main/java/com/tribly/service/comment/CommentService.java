package com.tribly.service.comment;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.BusinessException;
import com.tribly.domain.comment.Comment;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.comments.response.CommentListResponse;
import com.tribly.dto.error.ErrorCode;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.repository.comment.CommentRepository;
import com.tribly.service.post.PostService;
import com.tribly.service.ride.RideService;
import com.tribly.service.route.RouteService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.security.annotation.CheckAccess;
import com.tribly.service.team.TeamService;
import com.tribly.service.trip.TripService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class CommentService {

  @Inject CommentRepository commentRepository;

  @Inject TriblyQueryContext triblyQueryContext;
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
    User creator = triblyQueryContext.getUser();

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
