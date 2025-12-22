package com.tribly.domain.route.repository;

import com.tribly.domain.route.GpxTrack;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GpxTrackRepository implements PanacheRepository<GpxTrack> {

  /**
   * Find the GPX track associated with a route.
   * Each route should have at most one GPX track.
   */
  public GpxTrack findByRoute(Long routeId) {
    return find("route.id = ?1 and deleted = false", routeId).firstResult();
  }
}
