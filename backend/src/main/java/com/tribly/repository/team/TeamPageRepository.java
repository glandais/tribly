package com.tribly.repository.team;

import com.tribly.domain.team.TeamPage;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.common.TeamEntityRepository;
import com.tribly.repository.query.TriblyQuery;
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
  public TriblyQuery andSpecific(TriblyQuery triblyQuery, TeamPageQuery query) {
    boolean includeAbout = query.includeAbout() == null || query.includeAbout();
    if (!includeAbout) {
      triblyQuery = triblyQuery.and("te.aboutPage = false", Map.of());
    }
    return triblyQuery;
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
  public TeamPageQuery getQuerySlug(Long teamId, @Nullable Long userId, String slug) {
    return TeamPageQuery.builder().teamIds(Set.of(teamId)).userId(userId).slug(slug).build();
  }

  @Override
  public TeamPageQuery getQueryId(Long teamId, @Nullable Long userId, Long id) {
    return TeamPageQuery.builder().teamIds(Set.of(teamId)).userId(userId).id(id).build();
  }
}
