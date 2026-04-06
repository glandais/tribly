package fr.pedalons.repository.team;

import fr.pedalons.domain.team.TeamPage;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamPageRepository implements TeamEntityRepository<TeamPage, TeamPageQuery> {

  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.TEAM_PAGE;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.TEAM_PAGE;
  }

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, TeamPageQuery query) {
    boolean includeAbout = query.includeAbout() == null || query.includeAbout();
    if (!includeAbout) {
      pedalonsQuery = pedalonsQuery.and("te.aboutPage = false", Map.of());
    }
    return pedalonsQuery;
  }

  public long countAdditionalPages(Long teamId) {
    return count("team.id = ?1 AND aboutPage = false AND deleted = false", teamId);
  }

  public int getNextPageOrder(Long teamId) {
    Integer maxOrder =
        getEntityManager()
            .createQuery(
                "SELECT MAX(p.pageOrder) FROM TeamPage p WHERE p.team.id = ?1 AND p.aboutPage ="
                    + " false AND p.deleted = false",
                Integer.class)
            .setParameter(1, teamId)
            .getSingleResult();
    return maxOrder == null ? 0 : maxOrder + 1;
  }

  @Override
  public TeamPageQuery getQuerySlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug, boolean includeDeleted) {
    return TeamPageQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .build();
  }

  @Override
  public TeamPageQuery getQueryId(
      Long domainId, Long teamId, @Nullable Long userId, Long id, boolean includeDeleted) {
    return TeamPageQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .build();
  }
}
