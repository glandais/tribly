package fr.pedalons.service.team.response;

/**
 * The content counters a team card's clickable statistics row shows, beside the member count.
 *
 * <p>Bulk-loaded per page by {@code TeamStatsRepository}; never derived from the {@code Team} entity
 * itself, since the numbers depend on who is asking.
 */
public record TeamStats(long upcomingRideCount, long routeCount) {
  public static final TeamStats EMPTY = new TeamStats(0, 0);
}
