package com.tribly.service.route.request;

import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;

public record CreateRouteRequest(
    String name,
    String description,
    RouteDifficulty difficulty,
    SurfaceType surfaceType,
    Visibility visibility) {}
