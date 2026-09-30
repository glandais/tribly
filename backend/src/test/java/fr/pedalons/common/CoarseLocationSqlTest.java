package fr.pedalons.common;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.AdType;
import fr.pedalons.enums.Visibility;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The HQL function {@code coarse_location} is {@link CoarseLocation#blur} written a second time, in
 * SQL, for the classifieds proximity filter (docs/LEDGER_*.md SEC-8). The two must land every point
 * in the same cell: a filter measuring from a different cell than the one the API publishes would
 * either drop ads it should show or, worse, answer from a point finer than the published one.
 */
@QuarkusTest
class CoarseLocationSqlTest extends AbstractBaseTest {

  /** Well under a cell (~1 km); the SQL skips the six-decimal rounding of {@code blur}. */
  private static final double TOLERANCE = 1e-6;

  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject EntityManager entityManager;

  private Team team;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    dataService.getOrCreateDefaultDomain();
    user = dataService.createUser("coarse@example.com", "Coarse");
    team = dataService.createTeam(user, "Coarse Team", "coarse-team", Visibility.PUBLIC);
  }

  @Test
  void sqlAndJavaPutEveryPointInTheSameCell() {
    double[][] points = {
      {45.764043, 4.835659}, // Lyon
      {48.856614, 2.352222}, // Paris
      {43.296482, 5.369780}, // Marseille
      {69.649208, 18.955324}, // Tromsø: the cell widens in longitude
      {-33.868820, 151.209290}, // southern and eastern
      {40.712776, -74.005974}, // western
      {-22.906847, -43.172897}, // southern and western
      {45.769999, 4.839999}, // just under a latitude boundary
      {45.770001, 4.840001}, // just over it
      {0.000001, -0.000001}, // around the origin
      {88.5, 12.3}, // past the cos(lat) floor
    };
    for (double[] p : points) {
      Point<G2D> exact = point(WGS84, g(p[1], p[0]));
      Point<G2D> java = CoarseLocation.blur(exact);
      Point<G2D> sql = sqlBlur(p[0], p[1]);
      String where = p[0] + "," + p[1];
      assertEquals(java.getPosition().getLat(), sql.getPosition().getLat(), TOLERANCE, where);
      assertEquals(java.getPosition().getLon(), sql.getPosition().getLon(), TOLERANCE, where);
    }
  }

  @Test
  void anAdWithoutLocationStaysWithoutOne() {
    Ad ad = dataService.createAd(team, user, "Sans lieu", AdType.SALE);
    assertNull(blurOf(ad));
  }

  private Point<G2D> sqlBlur(double lat, double lon) {
    Ad ad = dataService.createAd(team, user, "Annonce " + lat + " " + lon, AdType.SALE);
    dataService.setAdDetails(ad, null, null, lat, lon);
    return blurOf(ad);
  }

  @SuppressWarnings("unchecked")
  private Point<G2D> blurOf(Ad ad) {
    return entityManager
        .createQuery(
            "select coarse_location(a.locationGeometry) from Ad a where a.id = :id", Point.class)
        .setParameter("id", ad.getId())
        .getSingleResult();
  }
}
