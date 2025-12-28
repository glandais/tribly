package com.tribly.dto.routes.request;

import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.validation.ValidateSchema;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Route update request")
@ValidateSchema
public record RouteRequest(
    @Schema(description = "Route name", required = true) String name,
    @Schema(description = "Media", required = true) MediaDto media,
    @Schema(description = "Surface type", required = true) SurfaceType surfaceType,
    @Schema(description = "Whether the route is publicly visible", required = true)
        Visibility visibility) {}
