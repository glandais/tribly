package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.common.repository.TriblyQuery;
import com.tribly.domain.route.Route;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;

@ApplicationScoped
public class RouteRepository implements BaseRepository<Route> {

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
    if (routeQuery.routeId() != null) {
      triblyQuery = triblyQuery.and("id = :routeId", Map.of("routeId", routeQuery.routeId()));
    }

    return getPage(triblyQuery, routeQuery);
  }
}
