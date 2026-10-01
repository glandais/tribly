package fr.pedalons.repository.tag;

import fr.pedalons.domain.tag.Tag;
import fr.pedalons.enums.TagTarget;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

/**
 * A team's tags. Keyed by team, and the team comes from {@code TeamService.getTeam}, which already
 * resolved it on the request's domain: every read here is bound to that domain through it.
 */
@ApplicationScoped
public class TagRepository implements BaseRepository<Tag> {

  /** The team's tags, of one kind or of all, alphabetically (plan D9). */
  public List<Tag> findByTeam(Long teamId, @Nullable TagTarget type) {
    if (type == null) {
      return list("team.id = ?1 order by lower(label), id", teamId);
    }
    return list("team.id = ?1 and type = ?2 order by lower(label), id", teamId, type);
  }

  public Optional<Tag> findByTeamAndId(Long teamId, Long id) {
    return find("team.id = ?1 and id = ?2", teamId, id).firstResultOptional();
  }

  /** The tags among {@code ids} that belong to this team and this kind. One query. */
  public List<Tag> findByTeamTypeAndIds(Long teamId, TagTarget type, Collection<Long> ids) {
    if (ids.isEmpty()) {
      return List.of();
    }
    return list("team.id = ?1 and type = ?2 and id in ?3", teamId, type, ids);
  }

  /** Whether the label is taken in this team and kind, case-insensitively, by another tag. */
  public boolean existsLabel(Long teamId, TagTarget type, String label, @Nullable Long excludedId) {
    if (excludedId == null) {
      return count("team.id = ?1 and type = ?2 and lower(label) = lower(?3)", teamId, type, label)
          > 0;
    }
    return count(
            "team.id = ?1 and type = ?2 and lower(label) = lower(?3) and id <> ?4",
            teamId,
            type,
            label,
            excludedId)
        > 0;
  }

  public long countByTeamAndType(Long teamId, TagTarget type) {
    return count("team.id = ?1 and type = ?2", teamId, type);
  }

  /**
   * How many live contents carry each of these tags: rides, posts, trips, routes and ads out of the
   * trash, plus ride templates. Two grouped queries, whatever the number of tags. A tag nobody uses
   * is absent from the map.
   */
  public Map<Long, Long> countUsage(Collection<Long> tagIds) {
    if (tagIds.isEmpty()) {
      return Map.of();
    }
    Map<Long, Long> counts = new HashMap<>();
    List<Object[]> entityRows =
        getEntityManager()
            .createQuery(
                "select l.tag.id, count(l.id) from TeamEntityTag l"
                    + " where l.tag.id in (:ids) and l.teamEntity.deleted = false"
                    + " group by l.tag.id",
                Object[].class)
            .setParameter("ids", tagIds)
            .getResultList();
    List<Object[]> templateRows =
        getEntityManager()
            .createQuery(
                "select l.tag.id, count(l.id) from RideTemplateTag l"
                    + " where l.tag.id in (:ids) group by l.tag.id",
                Object[].class)
            .setParameter("ids", tagIds)
            .getResultList();
    for (Object[] row : entityRows) {
      counts.merge((Long) row[0], ((Number) row[1]).longValue(), Long::sum);
    }
    for (Object[] row : templateRows) {
      counts.merge((Long) row[0], ((Number) row[1]).longValue(), Long::sum);
    }
    return counts;
  }
}
