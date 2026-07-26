package fr.pedalons.dto.routes.request;

import fr.pedalons.enums.*;
import fr.pedalons.service.team.request.MinRole;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

/**
 * @param view how much of each row to render. Not a filter — it changes nothing about which routes
 *     match, only how much of each one is written out, so the tile, the bounding box and the count
 *     leave it null.
 */
@Builder
public record RouteSearchParams(
    @Nullable String search,
    int page,
    int size,
    @Nullable MinRole minRole,
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
    @Nullable SortDirection sortDir,
    @Nullable ListViewMode view) {}
