package fr.pedalons.infrastructure.hibernate;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.query.sqm.function.SqmFunctionRegistry;
import org.hibernate.type.BasicType;
import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.StandardBasicTypes;

/**
 * Registers PostGIS functions that hibernate-spatial does not ship, so that route vector tiles and
 * route bounds can be produced by a single HQL query and therefore reuse the visibility clauses
 * built by {@code TeamEntityRepository} verbatim. Writing those queries in native SQL would mean
 * duplicating the clauses, and a drift between the copies would silently leak private routes.
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
   *
   * <p>The geometry is run through {@code ST_Simplify} before {@code ST_AsMVTGeom} so that a tile
   * carries no detail finer than one of its own pixels: the full-resolution GPX track (a point
   * every 10&nbsp;m) is emitted verbatim only once a pixel is smaller than that, and at low zoom the
   * far sparser simplified line keeps the tile light. The tolerance is
   * {@code SIMPLIFY_FACTOR} tile pixels in Web Mercator metres —
   * {@code SIMPLIFY_FACTOR * WEB_MERCATOR_CIRCUMFERENCE_M / (2^z * MVT_EXTENT)} — so it tracks
   * {@code ST_TileEnvelope}'s own resolution: at {@code SIMPLIFY_FACTOR = 1}, ~38&nbsp;m at z8,
   * below the 10&nbsp;m GPX step from ~z13 on (no visible loss when zoomed in). Raise
   * {@code SIMPLIFY_FACTOR} (2–4) to drop coarser detail and shrink tiles further at the cost of a
   * visibly blockier line; lower it toward 0 to keep more vertices. {@code preserveCollapsed = true}
   * keeps a minimal line for a track shorter than the tolerance rather than dropping the feature,
   * so no route ever vanishes from a tile.
   */
  /** Web Mercator (EPSG:3857) circumference in metres — the width of the world at z0. */
  private static final double WEB_MERCATOR_CIRCUMFERENCE_M = 40075016.6855785;

  /** Tile resolution passed to {@code ST_AsMVTGeom}; also the divisor turning it into pixels. */
  private static final int MVT_EXTENT = 4096;

  /**
   * Simplification tolerance in tile pixels. 1 removes only sub-pixel detail (invisible); raise to
   * 2–4 for lighter, blockier tiles.
   */
  private static final double SIMPLIFY_FACTOR = 1.0;

  /** Tolerance numerator as a plain decimal, so no scientific notation leaks into the SQL. */
  private static final String SIMPLIFY_NUMERATOR =
      java.math.BigDecimal.valueOf(SIMPLIFY_FACTOR * WEB_MERCATOR_CIRCUMFERENCE_M).toPlainString();

  private static final String ROUTE_MVT_PATTERN =
      "st_asmvt(cast(row("
          + "st_asmvtgeom(st_simplify(st_transform(?1, 3857), "
          + SIMPLIFY_NUMERATOR
          + " / (power(2, ?2) * "
          + MVT_EXTENT
          + "), true),"
          + " st_tileenvelope(?2, ?3, ?4), "
          + MVT_EXTENT
          + ", 64, true),"
          + " ?5, ?6, ?7, ?8, ?9) as route_mvt_row), 'routes')";

  /**
   * The corners of a geometry's bounding box. PostGIS reads them straight from the header it stores
   * alongside the geometry, so composing them with the plain HQL {@code min()} / {@code max()}
   * aggregates yields the extent of a route set without a dedicated composite type.
   */
  private static final String[] BBOX_FUNCTIONS = {"st_xmin", "st_ymin", "st_xmax", "st_ymax"};

  @Override
  public void contributeFunctions(FunctionContributions functionContributions) {
    SqmFunctionRegistry registry = functionContributions.getFunctionRegistry();
    BasicTypeRegistry types = functionContributions.getTypeConfiguration().getBasicTypeRegistry();

    registry.registerPattern(
        "route_mvt", ROUTE_MVT_PATTERN, types.resolve(StandardBasicTypes.BINARY));

    BasicType<Double> doubleType = types.resolve(StandardBasicTypes.DOUBLE);
    for (String name : BBOX_FUNCTIONS) {
      registry.registerPattern(name, name + "(?1)", doubleType);
    }
  }
}
