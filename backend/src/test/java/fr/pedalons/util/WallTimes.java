package fr.pedalons.util;

import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.common.EventDateTime;
import java.time.Instant;
import java.time.ZoneId;
import org.jspecify.annotations.Nullable;

/**
 * Requests carry wall times, never instants (docs/LEDGER_*.md API-60): these turn the instant a
 * test reasons in into the wall time a client would send for it.
 */
public final class WallTimes {

  /** The zone of a team created without one, which reads every entity no place locates. */
  public static final ZoneId DEFAULT_ZONE = ZoneId.of(Team.DEFAULT_TIMEZONE);

  private WallTimes() {}

  /**
   * {@code instant} as a wall time of the default team zone: the backend reads it back to the same
   * instant for an entity of such a team that no place locates — except in the second hour of an
   * autumn overlap, which it reads at the earlier offset.
   */
  public static @Nullable EventDateTime wall(@Nullable Instant instant) {
    return instant == null ? null : wallIn(instant, DEFAULT_ZONE);
  }

  /** {@code instant} as a wall time of {@code zone}. */
  public static EventDateTime wallIn(Instant instant, ZoneId zone) {
    return EventDateTime.local(instant.atZone(zone).toLocalDateTime());
  }
}
