package com.tribly.service.route.response;

import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

/**
 * Extracted route metadata from GPX.
 */
public record RouteMetadata(
    int distance, int elevationGain, int elevationLoss, Point<G2D> start, Point<G2D> end) {}
