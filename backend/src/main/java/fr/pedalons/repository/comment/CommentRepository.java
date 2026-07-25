package fr.pedalons.repository.comment;

import fr.pedalons.domain.comment.Comment;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class CommentRepository implements BaseRepository<Comment> {

  public List<Comment> findByTeamEntityId(Long teamEntityId) {
    return find("teamEntity.id = ?1 ORDER BY createdAt ASC", teamEntityId).list();
  }

  public Optional<Comment> findByTeamIdAndId(Long teamId, Long id) {
    return find(
            "teamEntity.team.id = ?1 AND teamEntity.team.deleted = false AND id = ?2", teamId, id)
        .firstResultOptional();
  }

  public List<Comment> findReplies(Long parentId) {
    return find("parent.id = ?1", parentId).list();
  }

  /** Comments a user wrote, for the GDPR data export. */
  public List<Comment> findByCreator(Long domainId, Long userId) {
    return list(
        "createdBy.id = ?2 and teamEntity.team.domain.id = ?1 order by createdAt",
        domainId,
        userId);
  }

  public long countByTeamEntityId(Long teamEntityId) {
    return count("teamEntity.id = ?1", teamEntityId);
  }
}
