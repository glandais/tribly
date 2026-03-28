package fr.pedalons.dto.routes.request;

import fr.pedalons.enums.*;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record RouteSearchParams(
    @Nullable String search,
    int page,
    int size,
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
    @Nullable RouteSortBy sortBy,
    @Nullable SortDirection sortDir) {}
