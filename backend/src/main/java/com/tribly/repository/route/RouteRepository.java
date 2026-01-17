package com.tribly.repository.route;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import com.tribly.domain.route.Route;
import com.tribly.enums.EntityType;
import com.tribly.enums.NearType;
import com.tribly.enums.SortDirection;
import com.tribly.enums.TeamEntityType;
import com.tribly.repository.common.TeamEntityRepository;
import com.tribly.repository.query.TriblyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Set;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RouteRepository implements TeamEntityRepository<Route, RouteQuery> {
  private static final int DEFAULT_NEAR_RADIUS = 25000;

  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.ROUTE;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.ROUTE;
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
    if (query.surfaceType() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.surfaceType = :surfaceType", Map.of("surfaceType", query.surfaceType()));
    }

    // Wind direction filter
    if (query.windDirection() != null) {
      triblyQuery =
          triblyQuery.and(
              "te.windDirection = :windDirection", Map.of("windDirection", query.windDirection()));
    }

    // Geographic proximity filter
    if (query.nearLat() != null && query.nearLon() != null) {
      Point<G2D> nearPoint = point(WGS84, g(query.nearLon(), query.nearLat()));
      double radius = query.nearRadius() != null ? query.nearRadius() : DEFAULT_NEAR_RADIUS;
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

  @Override
  public RouteQuery getQuerySlug(Long domainId, Long teamId, @Nullable Long userId, String slug) {
    return RouteQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .build();
  }

  @Override
  public RouteQuery getQueryId(Long domainId, Long teamId, @Nullable Long userId, Long id) {
    return RouteQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .build();
  }
}
