package com.tribly.domain.route.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.route.GpxTrack;
import jakarta.enterprise.context.ApplicationScoped;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class GpxTrackRepository implements BaseRepository<GpxTrack> {

  /**
   * Find the GPX track associated with a route.
   * Each route should have at most one GPX track.
   */
  @Nullable
  public GpxTrack findByRoute(Long routeId) {
    return find("route.id = ?1 and deleted = false", routeId).firstResult();
  }
}
