package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.route.RouteClimb;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class RouteClimbRepository implements BaseRepository<RouteClimb> {

  /**
   * Find all climbs for a route, ordered by start distance.
   */
  public List<RouteClimb> findByRoute(Long routeId) {
    return find("route.id = ?1 and deleted = false", Sort.by("startDistance").ascending(), routeId)
        .list();
  }
}
