package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.EntityWithSlugRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.common.repository.TriblyQuery;
import com.tribly.domain.route.Route;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class RouteRepository implements BaseRepository<Route>, EntityWithSlugRepository<Route> {

  @Override
  public Class<Route> getEntityClass() {
    return Route.class;
  }

  public TriblyPage<Route> find(RouteQuery routeQuery) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", routeQuery.teamId()))
            .and("deleted = false", Map.of())
            .order("updatedAt desc");
    if (routeQuery.visibility() != null) {
      triblyQuery =
          triblyQuery.and(
              "visibility = :visibility", Map.of("visibility", routeQuery.visibility()));
    }
    if (routeQuery.slug() != null) {
      triblyQuery = triblyQuery.and("slug = :slug", Map.of("slug", routeQuery.slug()));
    }
    if (routeQuery.search() != null && !routeQuery.search().isBlank()) {
      triblyQuery =
          triblyQuery.and(
              "LOWER(name) LIKE :search",
              Map.of("search", "%" + routeQuery.search().toLowerCase() + "%"));
    }

    return getPage(triblyQuery, routeQuery);
  }
}
