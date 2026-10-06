package fr.pedalons.service.calendar;

import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.infrastructure.timezone.TimezoneService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZoneId;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

/**
 * The timezone a stage's calendar days are counted in: that of its start place, else of its route's
 * first point, else Paris (docs/LEDGER_*.md API-90). An all-day event carries dates, not instants,
 * and a date only exists in a zone — UTC would put a stage leaving at 01:00 in Paris on the eve.
 *
 * <p>Paris is the fallback because every production team is French; the team's own zone takes its
 * place with docs/LEDGER_*.md API-60.
 */
@ApplicationScoped
public class StageTimezones {

  static final ZoneId FALLBACK = ZoneId.of("Europe/Paris");

  @Inject TimezoneService timezoneService;

  /** The zone of each stage, keyed by its id. */
  public Map<Long, ZoneId> of(Collection<TripStage> stages) {
    Map<Long, ZoneId> zones = new HashMap<>();
    for (TripStage stage : stages) {
      zones.put(stage.getId(), of(stage));
    }
    return zones;
  }

  public ZoneId of(TripStage stage) {
    Place start = stage.getStartPlace();
    Point<G2D> point = start == null ? null : usable(start.getGeometry());
    if (point == null) {
      Route route = stage.getRoute();
      point = route == null || route.isDeleted() ? null : usable(route.getStart());
    }
    if (point == null) {
      return FALLBACK;
    }
    G2D position = point.getPosition();
    return timezoneService.findZoneId(position.getLat(), position.getLon()).orElse(FALLBACK);
  }

  private static @Nullable Point<G2D> usable(@Nullable Point<G2D> point) {
    return point == null || point.isEmpty() ? null : point;
  }
}
