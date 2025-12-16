package com.tribly.domain.route;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class RouteClimbRepository implements PanacheRepository<RouteClimb> {

    /**
     * Find all climbs for a route, ordered by start distance.
     */
    public List<RouteClimb> findByRoute(Long routeId) {
        return find("route.id = ?1 and deleted = false",
                Sort.by("startDistance").ascending(), routeId)
                .list();
    }

    /**
     * Count climbs for a route.
     */
    public long countByRoute(Long routeId) {
        return count("route.id = ?1 and deleted = false", routeId);
    }

    /**
     * Find climbs by category for a route.
     */
    public List<RouteClimb> findByRouteAndCategory(Long routeId, ClimbCategory category) {
        return find("route.id = ?1 and category = ?2 and deleted = false",
                Sort.by("startDistance").ascending(), routeId, category)
                .list();
    }

    /**
     * Delete all climbs for a route (soft delete).
     */
    public void deleteByRoute(Long routeId) {
        update("deleted = true where route.id = ?1", routeId);
    }
}
