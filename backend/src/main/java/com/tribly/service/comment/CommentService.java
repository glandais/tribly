package com.tribly.service.comment;

import com.tribly.domain.comment.Comment;
import com.tribly.domain.comment.repository.CommentRepository;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.comments.request.CommentRequest;
import com.tribly.dto.comments.response.CommentDto;
import com.tribly.dto.comments.response.CommentListResponse;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.security.TeamSecurityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.jboss.logging.Logger;

@ApplicationScoped
public class CommentService {

  private static final Logger LOG = Logger.getLogger(CommentService.class);

  @Inject CommentRepository commentRepository;
  @Inject UserRepository userRepository;
  @Inject TeamSecurityService securityService;

  @Transactional
  public CommentListResponse listComments(Team team, Long teamEntityId, User user) {
    // Verify team membership
    securityService.requireMembership(user, team);

    List<Comment> allComments = commentRepository.findByTeamEntityId(teamEntityId);

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
  public CommentDto createComment(TeamEntity teamEntity, CommentRequest request, User author) {
    Team team = teamEntity.getTeam();

    // Verify team membership
    securityService.requireMembership(author, team);

    Comment parent = null;
    if (request.parentId() != null) {
      Long parentId = TsidUtils.toLong(request.parentId());
      parent =
          commentRepository
              .findByIdNotDeleted(parentId)
              .orElseThrow(() -> BusinessException.notFound("Comment", request.parentId()));

      // Enforce 1-level threading
      if (parent.getParent() != null) {
        throw BusinessException.validation("Replies cannot have replies");
      }

      // Ensure parent belongs to same entity
      if (!parent.getTeamEntity().getId().equals(teamEntity.getId())) {
        throw BusinessException.validation("Parent comment belongs to different entity");
      }
    }

    Comment comment =
        parent != null
            ? new Comment(author, team, teamEntity, parent, request.content())
            : new Comment(author, team, teamEntity, request.content());

    commentRepository.persistAndFlush(comment);

    LOG.infov("Comment created by user {0} on entity {1}", author.getId(), teamEntity.getId());

    return CommentDto.from(comment, List.of());
  }

  @Transactional
  public void deleteComment(Team team, Long commentId, User user) {
    Comment comment =
        commentRepository
            .findByIdNotDeleted(commentId)
            .orElseThrow(() -> BusinessException.notFound("Comment", commentId));

    // Check authorization: author can delete own, organizer/admin can delete any
    UserTeam membership = securityService.requireMembership(user, team);
    boolean isAuthor = comment.getCreatedBy().getId().equals(user.getId());
    boolean isOrganizerOrAdmin = membership.isOrganizer();

    if (!isAuthor && !isOrganizerOrAdmin) {
      throw BusinessException.forbidden("You cannot delete this comment");
    }

    // Soft delete the comment
    comment.setDeleted(true);
    commentRepository.persist(comment);

    // Cascade soft delete to replies
    int deletedReplies = commentRepository.softDeleteReplies(commentId);

    LOG.infov(
        "Comment {0} deleted by user {1} (cascade deleted {2} replies)",
        commentId, user.getId(), deletedReplies);
  }
}
