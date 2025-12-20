package com.tribly.domain.team.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.common.repository.TriblyQuery;
import com.tribly.domain.team.Team;
import com.tribly.enums.Visibility;
import com.tribly.service.team.response.TeamAndRole;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;

@Slf4j
@ApplicationScoped
public class TeamRepository implements BaseRepository<Team> {

  public Optional<Team> findBySlug(String slug) {
    return find("slug = ?1 and deleted = false", slug).firstResultOptional();
  }

  public boolean existsBySlug(String slug) {
    return count("slug = ?1 and deleted = false", slug) > 0;
  }

  public TriblyPage<TeamAndRole> find(TeamQuery teamQuery) {
    TriblyQuery triblyQuery =
        new TriblyQuery(
                "select t, ut.role,(SELECT COUNT(ut3) FROM UserTeam ut3 WHERE ut3.team.id = t.id"
                    + " AND ut3.deleted = false) from Team t left join UserTeam ut on ut.team.id ="
                    + " t.id AND ut.user.id = :userId AND ut.deleted = false WHERE")
            .and("t.deleted = false", Map.of())
            .order("name asc");
    triblyQuery.addParam("userId", teamQuery.userId());
    if (teamQuery.slug() != null) {
      triblyQuery.and("t.slug = :slug", Map.of("slug", teamQuery.slug()));
    }
    if (teamQuery.search() != null) {
      triblyQuery.and(
          "(lower(t.name) like :search or lower(t.description) like :search)",
          Map.of("search", teamQuery.search()));
    }
    if (teamQuery.userId() != null) {
      if (teamQuery.member() == null) {
        triblyQuery.and(
            "(t.visibility = :visibility OR ut.deleted = false)",
            Map.of("visibility", Visibility.PUBLIC));
      } else if (teamQuery.member()) {
        triblyQuery.and("ut.deleted = false", Map.of());
      } else {
        // Show public teams NOT joined by user
        triblyQuery.and("visibility = :visibility", Map.of("visibility", Visibility.PUBLIC));
        triblyQuery.and(
            "NOT EXISTS (SELECT 1 FROM UserTeam ut2 WHERE ut2.team.id = t.id AND ut2.user.id ="
                + " :userId AND ut2.deleted = false)",
            Map.of());
      }
    } else {
      triblyQuery.and("t.visibility = :visibility", Map.of("visibility", Visibility.PUBLIC));
    }
    String stringQuery = triblyQuery.getStringQuery();
    Map<String, @Nullable Object> params = triblyQuery.getParams();
    log.info("{} {}", stringQuery, params);
    PanacheQuery<TeamAndRole> panacheQuery = find(stringQuery, params).project(TeamAndRole.class);
    return getPage(panacheQuery, teamQuery.page(), teamQuery.size());
  }

  public Optional<TeamAndRole> findOne(String slug, @Nullable Long userId) {
    TriblyPage<TeamAndRole> page = find(new TeamQuery(0, 1, slug, userId, null, null));
    if (page.items().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(page.items().getFirst());
    }
  }
}
