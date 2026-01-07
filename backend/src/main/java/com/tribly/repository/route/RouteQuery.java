package com.tribly.repository.route;

import com.tribly.enums.*;
import com.tribly.repository.common.TeamEntityQueryInterface;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record RouteQuery(
    @Nullable Long userId,
    @Nullable Set<Long> teamIds,
    @Nullable Long id,
    @Nullable String slug,
    @Nullable String search,
    @Nullable Instant from,
    @Nullable Instant to,
    int page,
    int size,
    // Route-specific filters
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
    // Sorting
    @Nullable RouteSortBy sortBy,
    @Nullable SortDirection sortDir)
    implements TeamEntityQueryInterface {}
