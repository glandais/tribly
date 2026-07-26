package fr.pedalons.dto.routes.request;

import fr.pedalons.enums.Hilliness;
import fr.pedalons.enums.NearType;
import fr.pedalons.enums.SurfaceType;
import fr.pedalons.enums.WindDirection;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.jspecify.annotations.Nullable;

/**
 * The filters every route endpoint understands, as a single {@code @BeanParam}.
 *
 * <p>The same dozen query parameters are accepted by the listing, the vector tile, the bounding box
 * and the count, in a team-scoped and a cross-team variant — eight endpoints. Declaring them once
 * here is what keeps a filter from silently existing on the list and not on the count, which would
 * make a "Voir N parcours" button lie.
 *
 * <p>The query parameter names are exactly those the endpoints exposed before this class existed:
 * this is a refactor of the Java signatures, not of the HTTP contract.
 */
public class RouteFilterParams {

  @Parameter(description = "Search by name/markdown")
  @QueryParam("search")
  public @Nullable String search;

  @Parameter(description = "Minimum distance in meters")
  @QueryParam("minDistance")
  public @Nullable Float minDistance;

  @Parameter(description = "Maximum distance in meters")
  @QueryParam("maxDistance")
  public @Nullable Float maxDistance;

  @Parameter(description = "Minimum elevation gain in meters")
  @QueryParam("minElevationGain")
  public @Nullable Float minElevationGain;

  @Parameter(description = "Maximum elevation gain in meters")
  @QueryParam("maxElevationGain")
  public @Nullable Float maxElevationGain;

  @Parameter(description = "Hilliness preset (FLAT, HILLY, MOUNTAINOUS)")
  @QueryParam("hilliness")
  public @Nullable Hilliness hilliness;

  @Parameter(description = "Filter by surface type")
  @QueryParam("surfaceType")
  public @Nullable SurfaceType surfaceType;

  @Parameter(description = "Filter by wind direction")
  @QueryParam("windDirection")
  public @Nullable WindDirection windDirection;

  @Parameter(description = "Latitude for proximity search")
  @QueryParam("nearLat")
  public @Nullable Double nearLat;

  @Parameter(description = "Longitude for proximity search")
  @QueryParam("nearLon")
  public @Nullable Double nearLon;

  @Parameter(description = "Search radius in meters (default: 25000)")
  @QueryParam("nearRadius")
  public @Nullable Double nearRadius;

  @Parameter(description = "Search near START, END, or START_OR_END (default)")
  @QueryParam("nearType")
  public @Nullable NearType nearType;

  /**
   * Folds the bound parameters into the record the service layer takes. Subclasses add their own
   * fields on top rather than rebuilding the list.
   */
  public RouteSearchParams.RouteSearchParamsBuilder toBuilder() {
    return RouteSearchParams.builder()
        .search(search)
        .minDistance(minDistance)
        .maxDistance(maxDistance)
        .minElevationGain(minElevationGain)
        .maxElevationGain(maxElevationGain)
        .hilliness(hilliness)
        .surfaceType(surfaceType)
        .windDirection(windDirection)
        .nearLat(nearLat)
        .nearLon(nearLon)
        .nearRadius(nearRadius)
        .nearType(nearType);
  }

  public RouteSearchParams toSearchParams() {
    return toBuilder().build();
  }
}
