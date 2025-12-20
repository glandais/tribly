package com.tribly.dto.routes.request;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route update request")
@ValidateSchema
public record UpdateRouteRequest(
    @Nullable @Schema(description = "Route name", nullable = true) String name,
    @Nullable @Schema(description = "Route description", nullable = true) String description,
    @Nullable @Schema(description = "Route difficulty", nullable = true) RouteDifficulty difficulty,
    @Nullable @Schema(description = "Surface type", nullable = true) SurfaceType surfaceType,
    @Nullable @Schema(description = "Whether the route is publicly visible", nullable = true)
        Visibility visibility) {}
