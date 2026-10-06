package fr.pedalons.service.calendar;

import static org.geolatte.geom.builder.DSL.g;
import static org.geolatte.geom.builder.DSL.point;
import static org.geolatte.geom.crs.CoordinateReferenceSystems.WGS84;
import static org.junit.jupiter.api.Assertions.assertEquals;

import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.infrastructure.timezone.TimezoneService;
import java.time.ZoneId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** The zone a stage's all-day dates are counted in (docs/LEDGER_*.md API-90). */
class StageTimezonesTest {

  private static StageTimezones stageTimezones;

  @BeforeAll
  static void setUp() {
    stageTimezones = new StageTimezones();
    stageTimezones.timezoneService = new TimezoneService();
  }

  @Test
  void startPlace_givesTheZone() {
    TripStage stage = new TripStage();
    stage.setStartPlace(place(35.68, 139.76));
    stage.setRoute(route(48.85, 2.35));

    assertEquals(ZoneId.of("Asia/Tokyo"), stageTimezones.of(stage));
  }

  @Test
  void withoutStartPlace_theRouteStartGivesTheZone() {
    TripStage stage = new TripStage();
    stage.setRoute(route(40.71, -74.01));

    assertEquals(ZoneId.of("America/New_York"), stageTimezones.of(stage));
  }

  @Test
  void deletedRoute_isIgnored() {
    TripStage stage = new TripStage();
    Route route = route(40.71, -74.01);
    route.setDeleted(true);
    stage.setRoute(route);

    assertEquals(StageTimezones.FALLBACK, stageTimezones.of(stage));
  }

  @Test
  void nowhere_fallsBackToParis() {
    assertEquals(ZoneId.of("Europe/Paris"), stageTimezones.of(new TripStage()));
  }

  private static Place place(double lat, double lon) {
    Place place = new Place();
    place.setGeometry(point(WGS84, g(lon, lat)));
    return place;
  }

  private static Route route(double lat, double lon) {
    Route route = new Route();
    route.setStart(point(WGS84, g(lon, lat)));
    return route;
  }
}
