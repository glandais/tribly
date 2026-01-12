package com.tribly.service.route.response;

import com.tribly.enums.WindDirection;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

/**
 * Extracted route metadata from GPX.
 */
public record TrackMetadata(
    float distance,
    float elevationGain,
    float hilliness,
    float elevationLoss,
    Point<G2D> start,
    Point<G2D> end,
    @Nullable WindDirection windDirection) {}
