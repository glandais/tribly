package com.tribly.repository.team;

import com.tribly.domain.common.SearchClause;
import com.tribly.domain.team.Team;
import com.tribly.enums.TeamRole;
import com.tribly.enums.Visibility;
import com.tribly.repository.common.BaseRepository;
import com.tribly.repository.common.TriblyPage;
import com.tribly.repository.query.OrClause;
import com.tribly.repository.query.SimpleClause;
import com.tribly.repository.query.TriblyQuery;
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

  public Optional<Team> findBySlugAndDomain(Long domainId, String slug) {
    return find("domain.id = ?1 and slug = ?2 and deleted = false", domainId, slug)
        .firstResultOptional();
  }

  public boolean existsBySlugAndDomain(Long domainId, String slug) {
    return count("domain.id = ?1 and slug = ?2 and deleted = false", domainId, slug) > 0;
  }

  public boolean existsByDomain(Long domainId) {
    return count("domain.id = ?1 and deleted = false", domainId) > 0;
  }

  public TriblyPage<TeamAndRole> find(TeamQuery teamQuery) {
    TriblyQuery triblyQuery =
        new TriblyQuery(
                "select t, ut.role,(SELECT COUNT(ut3) FROM UserTeam ut3 WHERE ut3.team.id = t.id"
                    + " AND ut3.deleted = false) from Team t left join UserTeam ut on ut.team.id ="
                    + " t.id AND ut.user.id = :userId AND ut.deleted = false WHERE")
            .and("t.deleted = false", Map.of())
            .and("t.domain.id = :domainId", Map.of("domainId", teamQuery.domainId()))
            .order("name asc");
    triblyQuery.addParam("userId", teamQuery.userId());
    if (teamQuery.id() != null) {
      triblyQuery.and("t.id = :id", Map.of("id", teamQuery.id()));
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
    log.debug("{} {}", stringQuery, params);
    PanacheQuery<TeamAndRole> panacheQuery = find(stringQuery, params).project(TeamAndRole.class);
    return getPage(panacheQuery, teamQuery.page(), teamQuery.size());
  }

  public Optional<TeamAndRole> findOne(Long domainId, Long id, @Nullable Long userId) {
    TriblyPage<TeamAndRole> page =
        find(TeamQuery.builder().domainId(domainId).userId(userId).id(id).page(0).size(1).build());
    if (page.items().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(page.items().getFirst());
    }
  }
}
