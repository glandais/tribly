package com.tribly.domain.common.repository;

import com.tribly.domain.common.TeamEntity;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

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

  /**
   * Find publications across multiple teams with proper visibility filtering.
   */
  default TriblyPage<T> find(Q query) {
    // Build base query
    TriblyQuery triblyQuery =
        new TriblyQuery().and("deleted = false", Map.of()).order("dateTime desc");

    String slug = query.slug();
    if (slug != null) {
      triblyQuery = triblyQuery.and("slug = :slug", Map.of("slug", slug));
    }
    String search = query.search();
    if (search != null && !search.isBlank()) {
      triblyQuery =
          triblyQuery.and(
              "LOWER(name) LIKE :search", Map.of("search", "%" + search.toLowerCase() + "%"));
    }
    if (query.memberTeamIds().isEmpty()) {
      // Anonymous user: only public publications from public teams
      triblyQuery = triblyQuery.and("team.visibility = 'PUBLIC'", Map.of());
      triblyQuery = triblyQuery.and("visibility = 'PUBLIC'", Map.of());
      triblyQuery = triblyQuery.and("status IN ('PUBLISHED', 'CANCELLED')", Map.of());
    } else {
      // Authenticated user with team memberships
      triblyQuery =
          triblyQuery.and(
              """
              (
                (team.id IN :memberTeamIds AND (
                  status != 'DRAFT' OR team.id IN :organizerTeamIds
                ))
                OR
                (team.id NOT IN :memberTeamIds
                 AND team.visibility = 'PUBLIC'
                 AND visibility = 'PUBLIC'
                 AND status IN ('PUBLISHED', 'CANCELLED'))
              )
              """,
              Map.of(
                  "memberTeamIds",
                  query.memberTeamIds(),
                  "organizerTeamIds",
                  query.organizerTeamIds()));
    }

    // Add date range filters
    Instant from = query.from();
    if (from != null) {
      triblyQuery = triblyQuery.and("dateTime >= :from", Map.of("from", from));
    }
    Instant to = query.to();
    if (to != null) {
      triblyQuery = triblyQuery.and("dateTime <= :to", Map.of("to", to));
    }
    triblyQuery = andSpecific(triblyQuery, query);

    return getPage(triblyQuery, query);
  }

  default TriblyQuery andSpecific(TriblyQuery triblyQuery, Q query) {
    return triblyQuery;
  }
}
