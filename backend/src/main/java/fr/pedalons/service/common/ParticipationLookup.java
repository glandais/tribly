package fr.pedalons.service.common;

import fr.pedalons.dto.publications.response.UserParticipations;
import fr.pedalons.repository.ride.RideParticipationRepository;
import fr.pedalons.repository.trip.TripParticipationRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Resolves, in bulk, what the current user is registered to among a set of rides and trips.
 *
 * <p>Single entry point for every "am I registered?" field, on the detail path as well as on the
 * list path. The point is the plural: a page of publications resolves its participations with
 * <b>two</b> queries (one for rides, one for trips), never one per row. A per-row {@code
 * findByUserAndRide} would turn a 20-row feed into 20 extra round-trips, which is exactly the shape
 * of regression these lists were already cleaned of once.
 *
 * <p>Anonymous callers get {@link UserParticipations#NONE} without touching the database: the "me"
 * fields are then false/null, never an error.
 *
 * <p><b>Tenancy:</b> the lookup only ever answers about ids it was handed, and those ids come from
 * queries already filtered by {@code domainId} through {@code PedalonsQuery}. It widens nothing.
 */
@ApplicationScoped
public class ParticipationLookup {

  @Inject RideParticipationRepository rideParticipationRepository;

  @Inject TripParticipationRepository tripParticipationRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  /** Participations of the current user among these rides and trips. Two queries at most. */
  public UserParticipations forPublications(Collection<Long> rideIds, Collection<Long> tripIds) {
    return forPublications(pedalonsContext.getUserIdNullable(), rideIds, tripIds);
  }

  /**
   * Same, for a user resolved by the caller rather than by the request context.
   *
   * <p>The ICS feeds authenticate with a calendar token, not a session: {@code
   * PedalonsQueryContext} has no user there, and the "me" fields of a token-authenticated feed would
   * silently read as "not registered". The feed resolves its own user from the token and passes it
   * in here.
   *
   * @param userId {@code null} for an anonymous caller — answers {@link UserParticipations#NONE}
   *     without touching the database
   */
  public UserParticipations forPublications(
      @Nullable Long userId, Collection<Long> rideIds, Collection<Long> tripIds) {
    if (userId == null || (rideIds.isEmpty() && tripIds.isEmpty())) {
      return UserParticipations.NONE;
    }
    Map<Long, Long> rideGroups =
        rideParticipationRepository.findRegisteredGroupIdsByRideIds(userId, rideIds);
    Set<Long> trips = tripParticipationRepository.findRegisteredTripIds(userId, tripIds);
    return new UserParticipations(rideGroups, trips);
  }

  public UserParticipations forRide(Long rideId) {
    return forPublications(List.of(rideId), List.of());
  }

  public UserParticipations forTrip(Long tripId) {
    return forPublications(List.of(), List.of(tripId));
  }
}
