package com.tribly.domain.route.repository;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.route.Route;
import com.tribly.enums.NearType;
import com.tribly.enums.SortDirection;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

@ApplicationScoped
public class RouteRepository implements TeamEntityRepository<Route, RouteQuery> {
  private static final int DEFAULT_NEAR_RADIUS = 25000;

  @Override
  public String getTypeName() {
    return "Route";
  }

  @Override
  public TriblyQuery andSpecific(TriblyQuery triblyQuery, RouteQuery query) {
    // Distance range filter
    if (query.minDistance() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.distance >= :minDistance", Map.of("minDistance", query.minDistance()));
    }
    if (query.maxDistance() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.distance <= :maxDistance", Map.of("maxDistance", query.maxDistance()));
    }

    // Elevation gain range filter
    if (query.minElevationGain() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.elevationGain >= :minElevationGain",
              Map.of("minElevationGain", query.minElevationGain()));
    }
    if (query.maxElevationGain() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.elevationGain <= :maxElevationGain",
              Map.of("maxElevationGain", query.maxElevationGain()));
    }

    // Hilliness preset filter
    if (query.hilliness() != null && query.hilliness().getMinMetersPerKm() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.hilliness >= :minHilliness",
              Map.of("minHilliness", (double) query.hilliness().getMinMetersPerKm()));
    }
    if (query.hilliness() != null && query.hilliness().getMaxMetersPerKm() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.hilliness < :maxHilliness",
              Map.of("maxHilliness", (double) query.hilliness().getMaxMetersPerKm()));
    }

    // Surface type filter
    if (query.surfaceTypes() != null && !query.surfaceTypes().isEmpty()) {
      triblyQuery =
          triblyQuery.and(
              "te.surfaceType IN (:surfaceTypes)", Map.of("surfaceTypes", query.surfaceTypes()));
    }

    // Wind direction filter
    if (query.windDirections() != null && !query.windDirections().isEmpty()) {
      triblyQuery =
          triblyQuery.and(
              "te.windDirection IN (:windDirections)",
              Map.of("windDirections", query.windDirections()));
    }

    // Geographic proximity filter
    if (query.nearLat() != null && query.nearLon() != null) {
      Point<G2D> nearPoint = point(WGS84, g(query.nearLon(), query.nearLat()));
      int radius = query.nearRadius() != null ? query.nearRadius() : DEFAULT_NEAR_RADIUS;
      NearType nearType = query.nearType() != null ? query.nearType() : NearType.START_OR_END;

      String geoClause =
          switch (nearType) {
            case START -> "st_dwithin(te.start, :nearPoint, :nearRadius)";
            case END -> "st_dwithin(te.end, :nearPoint, :nearRadius)";
            case START_OR_END ->
                "(st_dwithin(te.start, :nearPoint, :nearRadius) OR st_dwithin(te.end, :nearPoint,"
                    + " :nearRadius))";
          };
      triblyQuery =
          triblyQuery.and(geoClause, Map.of("nearPoint", nearPoint, "nearRadius", radius));
    }

    // Custom sorting
    if (query.sortBy() != null) {
      SortDirection dir = query.sortDir() != null ? query.sortDir() : SortDirection.DESC;
      String orderClause = query.sortBy().getField() + " " + dir.name().toLowerCase();
      triblyQuery = triblyQuery.order(orderClause);
    }

    return triblyQuery;
  }
}
