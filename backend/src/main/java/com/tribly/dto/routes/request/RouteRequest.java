package com.tribly.dto.routes.request;

import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(description = "Route update request")
@ValidateSchema
public record RouteRequest(
    @Schema(description = "Route name", required = true) String name,
    @Nullable @Schema(description = "Route description", nullable = true) String description,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is publicly visible", required = true)
        Visibility visibility) {}
