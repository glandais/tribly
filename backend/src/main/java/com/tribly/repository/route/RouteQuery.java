package com.tribly.repository.route;

import com.tribly.enums.*;
import com.tribly.repository.common.TeamEntityQueryInterface;
import java.time.Instant;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record RouteQuery(
    Long domainId,
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
    @Nullable Float minDistance,
    @Nullable Float maxDistance,
    @Nullable Float minElevationGain,
    @Nullable Float maxElevationGain,
    @Nullable Hilliness hilliness,
    @Nullable SurfaceType surfaceType,
    @Nullable WindDirection windDirection,
    @Nullable Double nearLat,
    @Nullable Double nearLon,
    @Nullable Double nearRadius,
    @Nullable NearType nearType,
    // Sorting
    @Nullable RouteSortBy sortBy,
    @Nullable SortDirection sortDir)
    implements TeamEntityQueryInterface {}
