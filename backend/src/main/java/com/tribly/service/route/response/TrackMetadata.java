package com.tribly.service.route.response;

import com.tribly.enums.WindDirection;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

/**
 * Extracted route metadata from GPX.
 */
public record TrackMetadata(
    int distance,
    int elevationGain,
    int hilliness,
    int elevationLoss,
    Point<G2D> start,
    Point<G2D> end,
    @Nullable WindDirection windDirection) {}
