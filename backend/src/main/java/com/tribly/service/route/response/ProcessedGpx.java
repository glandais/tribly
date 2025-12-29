package com.tribly.service.route.response;

import com.tribly.domain.route.GpxTrack;
import io.github.glandais.gpx.climb.Climb;
import java.util.List;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;

/**
 * Result of GPX processing pipeline.
 */
public record ProcessedGpx(
    LineString<G2D> geometry,
    List<GpxTrack.TrackPoint> trackPoints,
    List<Climb> climbs,
    RouteMetadata metadata) {}
