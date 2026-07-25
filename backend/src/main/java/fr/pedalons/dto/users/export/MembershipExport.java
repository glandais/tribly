package fr.pedalons.dto.users.export;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.domain.ride.RideParticipation;
import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.domain.trip.TripParticipation;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

/** Team memberships and event participations — the user's relationships, not their authorship. */
public final class MembershipExport {

  private MembershipExport() {}

  /** {@code memberships/teams.json}. */
  public record Membership(
      String id,
      String teamSlug,
      String teamName,
      String role,
      Instant joinedAt,
      Instant createdAt) {

    public static Membership from(UserTeam ut) {
      return new Membership(
          TsidUtils.toString(ut.getId()),
          ut.getTeam().getSlug(),
          ut.getTeam().getName(),
          ut.getRole().name(),
          ut.getJoinedAt(),
          ut.getCreatedAt());
    }
  }

  /** {@code participations/rides.json}. */
  public record RideParticipationEntry(
      String id,
      String teamSlug,
      String rideSlug,
      String rideName,
      String groupName,
      Instant rideDateTime,
      Instant registeredAt) {

    public static RideParticipationEntry from(RideParticipation p) {
      var group = p.getRideGroup();
      var ride = group.getRide();
      return new RideParticipationEntry(
          TsidUtils.toString(p.getId()),
          ride.getTeam().getSlug(),
          ride.getSlug(),
          ride.getName(),
          group.getName(),
          ride.getDateTime(),
          p.getRegisteredAt());
    }
  }

  /** {@code participations/trips.json}. */
  public record TripParticipationEntry(
      String id,
      String teamSlug,
      String tripSlug,
      String tripName,
      Instant tripDateTime,
      Instant registeredAt) {

    public static TripParticipationEntry from(TripParticipation p) {
      var trip = p.getTrip();
      return new TripParticipationEntry(
          TsidUtils.toString(p.getId()),
          trip.getTeam().getSlug(),
          trip.getSlug(),
          trip.getName(),
          trip.getDateTime(),
          p.getRegisteredAt());
    }
  }

  /** An export entry whose bytes could not be read back from storage. */
  public record MissingFile(String pathInZip, @Nullable String reason) {}
}
