package fr.pedalons.infrastructure.timezone;

import jakarta.inject.Singleton;
import java.time.ZoneId;
import java.util.Optional;
import net.iakovlev.timeshape.TimeZoneEngine;

/**
 * Service for looking up timezone from GPS coordinates using the timeshape library. The engine is
 * initialized once at startup and provides fast in-memory timezone lookups from latitude/longitude.
 */
@Singleton
public class TimezoneService {

  private final TimeZoneEngine engine;

  public TimezoneService() {
    this.engine = TimeZoneEngine.initialize();
  }

  /**
   * Get the timezone for the given GPS coordinates.
   *
   * @param lat latitude
   * @param lon longitude
   * @return the ZoneId for the coordinates, or UTC if not found
   */
  public ZoneId getZoneId(double lat, double lon) {
    return findZoneId(lat, lon).orElse(ZoneId.of("UTC"));
  }

  /**
   * The timezone of the given GPS coordinates, empty at sea or anywhere outside every zone — for a
   * caller whose fallback is not UTC.
   */
  public Optional<ZoneId> findZoneId(double lat, double lon) {
    return engine.query(lat, lon);
  }
}
