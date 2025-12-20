package com.tribly.service.route.response;

import com.tribly.domain.route.GpxTrack;
import io.github.glandais.gpx.climb.Climb;
import java.util.List;

/**
 * Result of GPX processing pipeline.
 */
public record ProcessedGpx(
    String wkt,
    List<GpxTrack.TrackPoint> trackPoints,
    List<Climb> climbs,
    RouteMetadata metadata) {}
