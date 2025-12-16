package com.tribly.domain.route;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;

@ApplicationScoped
public class GpxTrackRepository implements PanacheRepository<GpxTrack> {

    /**
     * Find the GPX track associated with a route.
     * Each route should have at most one GPX track.
     */
    public Optional<GpxTrack> findByRoute(Long routeId) {
        return find("route.id = ?1 and deleted = false", routeId)
                .firstResultOptional();
    }

    /**
     * Check if a route has an associated GPX track.
     */
    public boolean existsByRoute(Long routeId) {
        return count("route.id = ?1 and deleted = false", routeId) > 0;
    }
}
