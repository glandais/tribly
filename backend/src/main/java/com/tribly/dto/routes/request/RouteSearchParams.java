package com.tribly.dto.routes.request;

import com.tribly.enums.*;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record RouteSearchParams(
    @Nullable String search,
    int page,
    int size,
    @Nullable Integer minDistance,
    @Nullable Integer maxDistance,
    @Nullable Integer minElevationGain,
    @Nullable Integer maxElevationGain,
    @Nullable Hilliness hilliness,
    @Nullable SurfaceType surfaceType,
    @Nullable WindDirection windDirection,
    @Nullable Double nearLat,
    @Nullable Double nearLon,
    @Nullable Integer nearRadius,
    @Nullable NearType nearType,
    @Nullable RouteSortBy sortBy,
    @Nullable SortDirection sortDir) {}
