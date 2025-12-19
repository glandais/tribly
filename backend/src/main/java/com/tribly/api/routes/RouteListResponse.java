package com.tribly.api.routes;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(description = "Paginated route list response")
public record RouteListResponse(
    @Schema(description = "List of routes", required = true) List<RouteDto> routes,
    @Schema(description = "Total number of routes", required = true) long total,
    @Schema(description = "Current page number", required = true) int page,
    @Schema(description = "Page size", required = true) int size) {}
