package fr.pedalons.infrastructure.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.StandardBasicTypes;

/**
 * Registers PostGIS functions that hibernate-spatial does not ship, so that route vector tiles can
 * be produced by a single HQL query and therefore reuse the visibility clauses built by {@code
 * TeamEntityRepository} verbatim. Writing the tile query in native SQL would mean duplicating those
 * clauses, and a drift between the two copies would silently leak private routes.
 *
 * <p>Registered via {@code META-INF/services/org.hibernate.boot.model.FunctionContributor}.
 */
public class PedalonsFunctionContributor implements FunctionContributor {

  /**
   * {@code route_mvt(geometry, z, x, y, slug, name, teamSlug, distance, elevationGain)} → the MVT
   * layer as a protobuf blob.
   *
   * <p>{@code ST_AsMVT} takes a whole row as its first argument, which HQL has no syntax for. The
   * row is rebuilt here from a composite type (see migration {@code V23__route_mvt_row_type.sql}),
   * whose column names become the MVT feature properties. Hibernate is unaware this is an
   * aggregate, so it renders it in the SELECT clause and Postgres folds every matching row into a
   * single tile — which also means no {@code GROUP BY}: one feature per GpxTrack, not per Route.
   */
  private static final String ROUTE_MVT_PATTERN =
      "st_asmvt(cast(row("
          + "st_asmvtgeom(st_transform(?1, 3857), st_tileenvelope(?2, ?3, ?4), 4096, 64, true),"
          + " ?5, ?6, ?7, ?8, ?9) as route_mvt_row), 'routes')";

  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    functionContributions
        .getFunctionRegistry()
        .registerPattern(
            "route_mvt",
            ROUTE_MVT_PATTERN,
            functionContributions
                .getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BINARY));
  }
}
