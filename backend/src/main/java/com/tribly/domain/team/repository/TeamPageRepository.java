package com.tribly.domain.team.repository;

import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.team.TeamPage;
import com.tribly.enums.EntityType;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class TeamPageRepository implements TeamEntityRepository<TeamPage, TeamPageQuery> {

  @Override
  public EntityType getEntityType() {
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
}
