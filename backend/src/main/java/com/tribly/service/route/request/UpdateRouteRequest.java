package com.tribly.service.route.request;

import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import org.jspecify.annotations.Nullable;

public record UpdateRouteRequest(
    @Nullable String name,
    @Nullable String description,
    @Nullable RouteDifficulty difficulty,
    @Nullable SurfaceType surfaceType,
    @Nullable Visibility visibility) {}
