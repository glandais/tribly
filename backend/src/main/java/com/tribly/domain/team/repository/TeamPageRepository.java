package com.tribly.domain.team.repository;

import com.tribly.domain.team.TeamPage;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeamPageRepository implements PanacheRepository<TeamPage> {

  public List<TeamPage> findAdditionalPagesByTeam(Long teamId) {
    return find(
            "team.id = ?1 AND aboutPage = false AND deleted = false", Sort.by("pageOrder"), teamId)
        .list();
  }

  public Optional<TeamPage> findByTeamAndSlug(Long teamId, String slug) {
    return find("team.id = ?1 AND slug = ?2 AND deleted = false", teamId, slug)
        .firstResultOptional();
  }

  public long countAdditionalPages(Long teamId) {
    return count("team.id = ?1 AND aboutPage = false AND deleted = false", teamId);
  }

  public boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 AND slug = ?2 AND deleted = false", teamId, slug) > 0;
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
