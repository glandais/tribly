package com.tribly.domain.team.repository;

import com.tribly.domain.common.SearchClause;
import com.tribly.domain.common.query.OrClause;
import com.tribly.domain.common.query.SimpleClause;
import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.service.team.request.MinRole;
import com.tribly.service.team.response.TeamAndRole;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    triblyQuery =
        SearchClause.addSearch(
            triblyQuery, Set.of("t.name", "t.aboutPage.markdown"), teamQuery.search());
    if (teamQuery.userId() != null) {
      if (teamQuery.minRole() == null || teamQuery.minRole() == MinRole.NOT_MEMBER) {
        OrClause or = new OrClause();

        or.add(
            new SimpleClause(
                "(t.visibility = :visibility OR ut.deleted = false)",
                Map.of("visibility", Visibility.PUBLIC)));
        or.add(new SimpleClause("ut.deleted = false", Map.of()));

        triblyQuery.and(or);

      } else {
        List<TeamRole> roles =
            switch (teamQuery.minRole()) {
              case MEMBER -> List.of(TeamRole.MEMBER, TeamRole.ORGANIZER, TeamRole.ADMIN);
              case ORGANIZER -> List.of(TeamRole.ORGANIZER, TeamRole.ADMIN);
              case ADMIN -> List.of(TeamRole.ADMIN);
              default ->
                  throw new IllegalStateException("Unexpected value: " + teamQuery.minRole());
            };
        triblyQuery.and("ut.deleted = false", Map.of());
        triblyQuery.and("ut.role in (:userRoles)", Map.of("userRoles", roles));
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
    TriblyPage<TeamAndRole> page =
        find(TeamQuery.builder().userId(userId).slug(slug).page(0).size(1).build());
    if (page.items().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(page.items().getFirst());
    }
  }
}
