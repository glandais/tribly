package com.tribly.repository.common;

import com.tribly.domain.common.SearchClause;
import com.tribly.domain.common.TeamEntity;
import com.tribly.enums.EntityType;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.query.AndClause;
import com.tribly.repository.query.OrClause;
import com.tribly.repository.query.SimpleClause;
import com.tribly.repository.query.TriblyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public interface TeamEntityRepository<T extends TeamEntity, Q extends TeamEntityQueryInterface>
    extends BaseRepository<T> {

  default boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 and slug = ?2", teamId, slug) > 0;
  }

  Q getQuerySlug(Long domainId, Long teamId, @Nullable Long userId, String slug);

  Q getQueryId(Long domainId, Long teamId, @Nullable Long userId, Long id);

  default Optional<T> findByTeamAndSlug(
      Long domainId, Long teamId, @Nullable Long userId, String slug) {
    return findOne(getQuerySlug(domainId, teamId, userId, slug));
  }

  default Optional<T> findByTeamAndId(Long domainId, Long teamId, @Nullable Long userId, Long id) {
    return findOne(getQueryId(domainId, teamId, userId, id));
  }

  TeamEntityType getEntityType();

  EntityType getAllEntityType();

  /**
   * Find publications across multiple teams with proper visibility filtering.
   */
  default TriblyPage<T> find(Q query) {
    TriblyQuery triblyQuery = getTriblyQuery(query);
    return getPage(triblyQuery, query);
  }

  default Optional<T> findOne(Q query) {
    TriblyQuery triblyQuery = getTriblyQuery(query);
    return findOne(triblyQuery);
  }

  default List<T> findAll(Q query) {
    TriblyQuery triblyQuery = getTriblyQuery(query);
    return findAll(triblyQuery);
  }

  private TriblyQuery getTriblyQuery(Q query) {
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
      triblyQuery = triblyQuery.and(publicEntity).and("TYPE(te) <> Ad", Map.of());
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

      // OR

      // team member and PUBLISHED/CANCELLED
      AndClause teamEntity = new AndClause();
      teamEntity.add(new SimpleClause("ut.deleted = false", Map.of()));
      teamEntity.add(new SimpleClause("te.status IN ('PUBLISHED', 'CANCELLED')", Map.of()));
      visibilityFilter.add(teamEntity);

      // OR

      // ad creator
      AndClause adCreator = new AndClause();
      // still member
      adCreator.add(new SimpleClause("ut.deleted = false", Map.of()));
      adCreator.add(new SimpleClause("TYPE(te) = Ad", Map.of()));
      // userId already defined
      adCreator.add(new SimpleClause("te.createdBy.id = :userId", Map.of()));
      visibilityFilter.add(adCreator);

      // OR

      // organizer
      AndClause organizer = new AndClause();
      organizer.add(new SimpleClause("ut.role = 'ORGANIZER'", Map.of()));
      // can't see DRAFT TeamPage
      organizer.add(new SimpleClause("TYPE(te) <> TeamPage", Map.of()));
      // can't see DRAFT Ads
      organizer.add(new SimpleClause("TYPE(te) <> Ad", Map.of()));
      visibilityFilter.add(organizer);

      // OR

      // admin
      AndClause draftEntity = new AndClause();
      draftEntity.add(new SimpleClause("ut.role = 'ADMIN'", Map.of()));
      // can't see DRAFT Ads
      draftEntity.add(new SimpleClause("TYPE(te) <> Ad", Map.of()));
      visibilityFilter.add(draftEntity);

      triblyQuery.and(visibilityFilter);
    }

    triblyQuery =
        triblyQuery
            // filter by domain
            .and("te.team.domain.id = :domainId", Map.of("domainId", query.domainId()))
            // entity not deleted
            .and("te.deleted = false", Map.of())
            // team not deleted
            .and("te.team.deleted = false", Map.of())
            // not trip or trip enabled
            .and("(TYPE(te) <> Trip OR te.team.enableTrips = true)", Map.of())
            // not ad or ad enabled
            .and("(TYPE(te) <> Ad OR te.team.enableAds = true)", Map.of())
            .order("dateTime desc");

    Set<Long> teamIds = query.teamIds();
    if (teamIds != null && !teamIds.isEmpty()) {
      triblyQuery = triblyQuery.and("te.team.id IN (:teamIds)", Map.of("teamIds", teamIds));
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
    return triblyQuery;
  }

  default TriblyQuery andSpecific(TriblyQuery triblyQuery, Q query) {
    return triblyQuery;
  }
}
