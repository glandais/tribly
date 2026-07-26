package fr.pedalons.dto.publications.response;

import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * What the current user is registered to, among a known set of rides and trips, loaded once for a
 * whole page (or once for a single detail row).
 *
 * <p>This is the "me" half of a publication payload: {@code registered} and {@code
 * registeredGroupId} depend on who is asking, while {@link PublicationListSummaries} does not.
 * Keeping the two apart is what lets the summaries stay a pure per-entity aggregate.
 *
 * <p>Built by {@code fr.pedalons.service.common.ParticipationLookup}, which resolves the current
 * user itself — an anonymous caller gets {@link #NONE} and every "me" field reads false/null rather
 * than failing.
 *
 * @param registeredGroupIdByRideId ride id → the group of that ride the user joined; a ride absent
 *     from the map is a ride the user did not join
 * @param registeredTripIds the trip ids the user joined
 */
public record UserParticipations(
    Map<Long, Long> registeredGroupIdByRideId, Set<Long> registeredTripIds) {

  /** Nobody is registered to anything — what an anonymous caller sees. */
  public static final UserParticipations NONE = new UserParticipations(Map.of(), Set.of());

  /** The group of this ride the user joined, or {@code null} if they did not join it. */
  public @Nullable Long registeredGroupId(Long rideId) {
    return registeredGroupIdByRideId.get(rideId);
  }

  public boolean isRegisteredToRide(Long rideId) {
    return registeredGroupIdByRideId.containsKey(rideId);
  }

  public boolean isRegisteredToTrip(Long tripId) {
    return registeredTripIds.contains(tripId);
  }
}
