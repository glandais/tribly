package com.tribly.api.routes;

import com.tribly.domain.common.Visibility;
import com.tribly.domain.route.RouteDifficulty;
import com.tribly.domain.route.SurfaceType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route update request")
public record UpdateRouteRequest(
    @Nullable @Schema(description = "Route name", nullable = true) String name,
    @Nullable @Schema(description = "Route description", nullable = true) String description,
    @Nullable @Schema(description = "Route difficulty", nullable = true) RouteDifficulty difficulty,
    @Nullable @Schema(description = "Surface type", nullable = true) SurfaceType surfaceType,
    @Nullable @Schema(description = "Whether the route is publicly visible", nullable = true)
        Visibility visibility) {}
