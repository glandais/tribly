package com.tribly.service.route.response;

/**
 * Extracted route metadata from GPX.
 */
public record RouteMetadata(
    int distance,
    int elevationGain,
    int elevationLoss,
    double startLat,
    double startLng,
    double endLat,
    double endLng) {}
