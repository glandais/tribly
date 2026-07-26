package fr.pedalons.service.comment;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.comment.Comment;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.comments.request.CommentListQuery;
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

  /** The whole comment tree, as this endpoint has always answered. */
  @Transactional
  @CheckAccess(entityType = EntityType.COMMENT, action = ActionType.LIST)
  public CommentListResponse listComments(String teamSlug, String slug, EntityType entityType) {
    return listComments(teamSlug, slug, entityType, CommentListQuery.ALL);
  }

  /**
   * Lists the comments of one entity, in one of three modes.
   *
   * <ul>
   *   <li><b>Unparameterized</b> ({@link CommentListQuery#ALL}): the whole tree in a single query,
   *       byte for byte what the endpoint returned before it took parameters. The web client still
   *       calls it that way, so this path must not change.
   *   <li><b>Paginated roots</b>: a page of top-level comments with their replies attached. The
   *       pagination is on the <em>roots</em> — paginating the flat comment list would cut threads in
   *       half and give "page 2" no meaning. Four queries, whatever the page size.
   *   <li><b>One thread</b> ({@code parentId}): a page of the replies of a single comment, for a
   *       client that renders a collapsed thread and expands it on demand.
   * </ul>
   *
   * <p>The {@code parentId} of the thread mode is matched against the resolved entity, so a comment
   * id belonging to another entity — hence another team, hence possibly another domain — yields an
   * empty page rather than someone else's thread.
   */
  @Transactional
  @CheckAccess(entityType = EntityType.COMMENT, action = ActionType.LIST)
  public CommentListResponse listComments(
      String teamSlug, String slug, EntityType entityType, CommentListQuery query) {
    Team team = teamService.getTeam(teamSlug);
    TeamEntity teamEntity = getTeamEntity(team, slug, entityType);
    Long teamEntityId = teamEntity.getId();

    if (!query.paginated()) {
      return wholeTree(teamEntityId);
    }
    Long parentId = query.parentId();
    if (parentId != null) {
      return threadPage(teamEntityId, parentId, query);
    }
    return rootPage(teamEntityId, query);
  }

  /** One query, the tree built in memory — the shape every existing caller depends on. */
  private CommentListResponse wholeTree(Long teamEntityId) {
    List<Comment> allComments = commentRepository.findByTeamEntityId(teamEntityId);

    Map<Long, List<Comment>> repliesByParentId = groupByParent(allComments);

    List<Comment> roots = allComments.stream().filter(c -> c.getParent() == null).toList();
    List<CommentDto> dtos = roots.stream().map(root -> toDto(root, repliesByParentId)).toList();

    return new CommentListResponse(dtos, allComments.size(), roots.size(), 0, roots.size());
  }

  private CommentListResponse rootPage(Long teamEntityId, CommentListQuery query) {
    List<Comment> roots =
        commentRepository.pageRoots(teamEntityId, query.page(), query.size(), query.sort());
    // Bounded by the page size, so the replies of a whole page cost one query — not one per root.
    Map<Long, List<Comment>> repliesByParentId =
        groupByParent(
            commentRepository.findRepliesByParentIds(
                roots.stream().map(Comment::getId).toList(), query.sort()));

    List<CommentDto> dtos = roots.stream().map(root -> toDto(root, repliesByParentId)).toList();

    return new CommentListResponse(
        dtos,
        (int) commentRepository.countByTeamEntityId(teamEntityId),
        (int) commentRepository.countRoots(teamEntityId),
        query.page(),
        query.size());
  }

  private CommentListResponse threadPage(Long teamEntityId, Long parentId, CommentListQuery query) {
    List<CommentDto> dtos =
        commentRepository
            .pageReplies(teamEntityId, parentId, query.page(), query.size(), query.sort())
            .stream()
            .map(reply -> CommentDto.from(reply, List.of()))
            .toList();

    return new CommentListResponse(
        dtos,
        (int) commentRepository.countByTeamEntityId(teamEntityId),
        (int) commentRepository.countReplies(teamEntityId, parentId),
        query.page(),
        query.size());
  }

  private static Map<Long, List<Comment>> groupByParent(List<Comment> comments) {
    return comments.stream()
        .filter(c -> c.getParent() != null)
        .collect(Collectors.groupingBy(c -> c.getParent().getId()));
  }

  private static CommentDto toDto(Comment root, Map<Long, List<Comment>> repliesByParentId) {
    List<CommentDto> replies =
        repliesByParentId.getOrDefault(root.getId(), List.of()).stream()
            .map(reply -> CommentDto.from(reply, List.of()))
            .toList();
    return CommentDto.from(root, replies);
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
