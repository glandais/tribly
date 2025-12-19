package com.tribly.domain.route;

import com.tribly.domain.common.BaseRepository;
import com.tribly.domain.common.TriblyPage;
import com.tribly.domain.common.TriblyQuery;
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
