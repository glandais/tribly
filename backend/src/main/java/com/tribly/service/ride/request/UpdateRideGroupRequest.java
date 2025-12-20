package com.tribly.service.ride.request;

import org.jspecify.annotations.Nullable;

public record UpdateRideGroupRequest(
    @Nullable String name,
    @Nullable String description,
    @Nullable Integer averageSpeed,
    @Nullable Integer maxParticipants,
    @Nullable Long routeId) {}
