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
   * The timezone of the given GPS coordinates, empty at sea or anywhere outside every zone. No UTC
   * fallback: the caller's is the team's zone (docs/LEDGER_*.md API-60, plan §4).
   */
  public Optional<ZoneId> findZoneId(double lat, double lon) {
    return engine.query(lat, lon);
  }
}
