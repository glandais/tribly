package fr.pedalons.repository.route;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;

import fr.pedalons.domain.route.Route;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.NearType;
import fr.pedalons.enums.SortDirection;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.infrastructure.geom.TileUtils;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class RouteRepository implements TeamEntityRepository<Route, RouteQuery> {
  private static final int DEFAULT_NEAR_RADIUS = 25000;

  private static final byte[] EMPTY_TILE = new byte[0];

  /**
   * Walks the tracks rather than the routes, so the geometry is at hand, but still binds {@code te}
   * to the route: the visibility clauses apply unchanged. {@code route_mvt} is an aggregate (see
   * {@code PedalonsFunctionContributor}), hence no ordering.
   */
  private static final QueryShape MVT_SHAPE =
      new QueryShape(
          "route_mvt(gt.geometry, :z, :x, :y, te.slug, te.name, te.team.slug, te.distance,"
              + " te.elevationGain)",
          "GpxTrack gt join gt.route te",
          false);

  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.ROUTE;
  }

  /**
   * Renders the routes matching {@code query} as a Mapbox vector tile.
   *
   * @return the protobuf layer, or an empty array when no route intersects the tile
   */
  public byte[] mvtTile(RouteQuery query, int z, int x, int y) {
    PedalonsQuery pedalonsQuery = getPedalonsQuery(query, true, MVT_SHAPE);
    pedalonsQuery
        .and(
            "st_intersects(gt.geometry, :tileEnvelope) = true",
            Map.of("tileEnvelope", TileUtils.tileEnvelope4326(z, x, y)))
        .addParam("z", z)
        .addParam("x", x)
        .addParam("y", y);

    TypedQuery<byte[]> typedQuery =
        getEntityManager().createQuery(pedalonsQuery.getStringQuery(), byte[].class);
    pedalonsQuery.getParams().forEach(typedQuery::setParameter);

    // Aggregating over zero rows still yields one row, holding an empty tile.
    List<byte[]> result = typedQuery.getResultList();
    byte[] tile = result.isEmpty() ? null : result.getFirst();
    return tile == null ? EMPTY_TILE : tile;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.ROUTE;
  }

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, RouteQuery query) {
    // Routes enabled filter
    pedalonsQuery = pedalonsQuery.and("te.team.enableRoutes = true", Map.of());

    // Distance range filter
    if (query.minDistance() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.distance >= :minDistance", Map.of("minDistance", query.minDistance()));
    }
    if (query.maxDistance() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.distance <= :maxDistance", Map.of("maxDistance", query.maxDistance()));
    }

    // Elevation gain range filter
    if (query.minElevationGain() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.elevationGain >= :minElevationGain",
              Map.of("minElevationGain", query.minElevationGain()));
    }
    if (query.maxElevationGain() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.elevationGain <= :maxElevationGain",
              Map.of("maxElevationGain", query.maxElevationGain()));
    }

    // Hilliness preset filter
    if (query.hilliness() != null && query.hilliness().getMinMetersPerKm() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.hilliness >= :minHilliness",
              Map.of("minHilliness", (double) query.hilliness().getMinMetersPerKm()));
    }
    if (query.hilliness() != null && query.hilliness().getMaxMetersPerKm() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.hilliness < :maxHilliness",
              Map.of("maxHilliness", (double) query.hilliness().getMaxMetersPerKm()));
    }

    // Surface type filter
    if (query.surfaceType() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.surfaceType = :surfaceType", Map.of("surfaceType", query.surfaceType()));
    }

    // Wind direction filter
    if (query.windDirection() != null) {
      pedalonsQuery =
          pedalonsQuery.and(
              "te.windDirection = :windDirection", Map.of("windDirection", query.windDirection()));
    }

    // Geographic proximity filter
    if (query.nearLat() != null && query.nearLon() != null) {
      Point<G2D> nearPoint = point(WGS84, g(query.nearLon(), query.nearLat()));
      double radius = query.nearRadius() != null ? query.nearRadius() : DEFAULT_NEAR_RADIUS;
      NearType nearType = query.nearType() != null ? query.nearType() : NearType.START_OR_END;

      String geoClause =
          switch (nearType) {
            case START -> "st_distancesphere(te.start, :nearPoint) <= :nearRadius";
            case END -> "st_distancesphere(te.end, :nearPoint) <= :nearRadius";
            case START_OR_END ->
                "(st_distancesphere(te.start, :nearPoint) <= :nearRadius OR"
                    + " st_distancesphere(te.end, :nearPoint) <= :nearRadius)";
          };
      pedalonsQuery =
          pedalonsQuery.and(geoClause, Map.of("nearPoint", nearPoint, "nearRadius", radius));
    }

    // Custom sorting
    if (query.sortBy() != null) {
      SortDirection dir = query.sortDir() != null ? query.sortDir() : SortDirection.DESC;
      String orderClause = query.sortBy().getField() + " " + dir.name().toLowerCase();
      pedalonsQuery = pedalonsQuery.order(orderClause);
    }

    return pedalonsQuery;
  }

  @Override
  public RouteQuery getQuerySlug(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return RouteQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public RouteQuery getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return RouteQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }
}
