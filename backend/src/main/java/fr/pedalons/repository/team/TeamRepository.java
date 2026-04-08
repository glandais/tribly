package fr.pedalons.repository.team;

import fr.pedalons.domain.common.SearchClause;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.enums.Visibility;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.OrClause;
import fr.pedalons.repository.query.PedalonsQuery;
import fr.pedalons.repository.query.SimpleClause;
import fr.pedalons.service.team.response.TeamAndRole;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.logging.Log;
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

  public PedalonsPage<TeamAndRole> find(TeamQuery teamQuery) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery(
                "select t, ut.role,(SELECT COUNT(ut3) FROM UserTeam ut3 WHERE ut3.team.id ="
                    + " t.id) from Team t left join UserTeam ut on ut.team.id ="
                    + " t.id AND ut.user.id = :userId WHERE")
            .and("t.domain.id = :domainId", Map.of("domainId", teamQuery.domainId()))
            .order("name asc");
    if (!teamQuery.platformAdmin()) {
      pedalonsQuery.and("t.deleted = false", Map.of());
    }
    pedalonsQuery.addParam("userId", teamQuery.userId());
    if (teamQuery.id() != null) {
      pedalonsQuery.and("t.id = :id", Map.of("id", teamQuery.id()));
    }
    pedalonsQuery =
        SearchClause.addSearch(
            pedalonsQuery, Set.of("t.name", "t.aboutPage.markdown"), teamQuery.search());
    if (teamQuery.userId() != null) {
      if (teamQuery.platformAdmin()) {
        Log.debugf("Platform admin, all teams");
      } else if (teamQuery.minRole() == null) {
        OrClause or = new OrClause();

        or.add(
            new SimpleClause(
                "(t.visibility <> :visibility OR ut IS NOT NULL)",
                Map.of("visibility", Visibility.TEAM)));
        or.add(new SimpleClause("ut IS NOT NULL", Map.of()));

        pedalonsQuery.and(or);

      } else {
        List<TeamRole> roles =
            switch (teamQuery.minRole()) {
              case MEMBER -> List.of(TeamRole.MEMBER, TeamRole.ORGANIZER, TeamRole.ADMIN);
              case ORGANIZER -> List.of(TeamRole.ORGANIZER, TeamRole.ADMIN);
              case ADMIN -> List.of(TeamRole.ADMIN);
              default ->
                  throw new IllegalStateException("Unexpected value: " + teamQuery.minRole());
            };
        pedalonsQuery.and("ut.role in (:userRoles)", Map.of("userRoles", roles));
      }
    } else {
      pedalonsQuery.and("t.visibility <> :visibility", Map.of("visibility", Visibility.TEAM));
    }
    String stringQuery = pedalonsQuery.getStringQuery();
    Map<String, @Nullable Object> params = pedalonsQuery.getParams();
    log.debug("{} {}", stringQuery, params);
    PanacheQuery<TeamAndRole> panacheQuery = find(stringQuery, params).project(TeamAndRole.class);
    return getPage(panacheQuery, teamQuery.page(), teamQuery.size());
  }

  public Optional<TeamAndRole> findOne(
      Long domainId, Long id, @Nullable Long userId, boolean platformAdmin) {
    PedalonsPage<TeamAndRole> page =
        find(
            TeamQuery.builder()
                .domainId(domainId)
                .userId(userId)
                .id(id)
                .page(0)
                .size(1)
                .platformAdmin(platformAdmin)
                .build());
    if (page.items().isEmpty()) {
      return Optional.empty();
    } else {
      return Optional.of(page.items().getFirst());
    }
  }
}
