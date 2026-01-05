package com.tribly.domain.common.repository;

import com.tribly.domain.common.SearchClause;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.query.*;
import com.tribly.enums.EntityType;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public interface TeamEntityRepository<T extends TeamEntity, Q extends TeamEntityQueryInterface>
    extends BaseRepository<T> {

  default boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 and slug = ?2", teamId, slug) > 0;
  }

  default Optional<T> findByTeamAndSlug(Long teamId, String slug) {
    return find("team.id = ?1 and slug = ?2 and deleted = false", teamId, slug)
        .firstResultOptional();
  }

  EntityType getEntityType();

  /**
   * Find publications across multiple teams with proper visibility filtering.
   */
  default TriblyPage<T> find(Q query) {
    // Build base query
    TriblyQuery triblyQuery;

    AndClause publicEntity = new AndClause();
    publicEntity.add(new SimpleClause("te.team.visibility = 'PUBLIC'", Map.of()));
    publicEntity.add(new SimpleClause("te.visibility = 'PUBLIC'", Map.of()));
    publicEntity.add(new SimpleClause("te.status IN ('PUBLISHED', 'CANCELLED')", Map.of()));

    if (query.userId() == null) {
      triblyQuery =
          new TriblyQuery("select te from " + getEntityType().getTypeName() + " te where");

      // Anonymous user: only public publications from public teams
      triblyQuery = triblyQuery.and(publicEntity);
    } else {
      triblyQuery =
          new TriblyQuery(
                  "select te from "
                      + getEntityType().getTypeName()
                      + " te left join UserTeam ut on ut.team.id = te.team.id and ut.user.id ="
                      + " :userId and ut.deleted = false where")
              .addParam("userId", query.userId());

      OrClause visibilityFilter = new OrClause();

      // public
      visibilityFilter.add(publicEntity);

      AndClause teamEntity = new AndClause();
      teamEntity.add(new SimpleClause("ut.deleted = false", Map.of()));
      teamEntity.add(new SimpleClause("te.status IN ('PUBLISHED', 'CANCELLED')", Map.of()));
      visibilityFilter.add(teamEntity);

      AndClause draftEntity = new AndClause();
      draftEntity.add(new SimpleClause("ut.role IN ('ORGANIZER', 'ADMIN')", Map.of()));
      visibilityFilter.add(draftEntity);

      triblyQuery.and(visibilityFilter);
    }

    triblyQuery =
        triblyQuery
            .and("te.deleted = false", Map.of())
            .and("te.team.deleted = false", Map.of())
            .and("(TYPE(te) <> Trip OR te.team.enableTrips = true)", Map.of())
            .and("(TYPE(te) <> Ad OR te.team.enableAds = true)", Map.of())
            .order("dateTime desc");

    Set<String> teamSlugs = query.teamSlugs();
    if (teamSlugs != null && !teamSlugs.isEmpty()) {
      triblyQuery = triblyQuery.and("te.team.slug IN (:teamSlugs)", Map.of("teamSlugs", teamSlugs));
    }
    String slug = query.slug();
    if (slug != null) {
      triblyQuery = triblyQuery.and("te.slug = :slug", Map.of("slug", slug));
    }
    Long id = query.id();
    if (id != null) {
      triblyQuery = triblyQuery.and("te.id = :id", Map.of("id", id));
    }
    triblyQuery =
        SearchClause.addSearch(triblyQuery, Set.of("te.name", "te.markdown"), query.search());

    // Add date range filters
    Instant from = query.from();
    if (from != null) {
      triblyQuery = triblyQuery.and("te.dateTime >= :from", Map.of("from", from));
    }
    Instant to = query.to();
    if (to != null) {
      triblyQuery = triblyQuery.and("te.dateTime <= :to", Map.of("to", to));
    }
    triblyQuery = andSpecific(triblyQuery, query);

    return getPage(triblyQuery, query);
  }

  default TriblyQuery andSpecific(TriblyQuery triblyQuery, Q query) {
    return triblyQuery;
  }
}
