package com.tribly.dto.routes.request;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;

@ValidateSchema
public record CreateRouteRequest(
    String name,
    String description,
    RouteDifficulty difficulty,
    SurfaceType surfaceType,
    Visibility visibility) {}
