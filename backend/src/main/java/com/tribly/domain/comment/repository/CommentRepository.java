package com.tribly.domain.comment.repository;

import com.tribly.domain.comment.Comment;
import com.tribly.domain.common.repository.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CommentRepository implements BaseRepository<Comment> {

  public List<Comment> findByTeamEntityId(Long teamEntityId) {
    return find("teamEntity.id = ?1 AND deleted = false ORDER BY createdAt ASC", teamEntityId)
        .list();
  }

  public Optional<Comment> findByIdNotDeleted(Long id) {
    return find("id = ?1 AND deleted = false", id).firstResultOptional();
  }

  public List<Comment> findReplies(Long parentId) {
    return find("parent.id = ?1 AND deleted = false", parentId).list();
  }

  public int softDeleteReplies(Long parentId) {
    return update("deleted = true WHERE parent.id = ?1 AND deleted = false", parentId);
  }

  public long countByTeamEntityId(Long teamEntityId) {
    return count("teamEntity.id = ?1 AND deleted = false", teamEntityId);
  }
}
