package fr.pedalons.service.timezone;

import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.ride.RideGroup;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import fr.pedalons.service.publication.PublicationEndCalculator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * What a change of a team's zone does to its content (docs/LEDGER_*.md API-60, plan §9): every
 * ride, trip, stage and post whose stored zone is the old team zone and whose chain finds no point
 * moves to the new zone, so that its stored zone stays the resolved one. Left behind, the next save
 * of the entity, even for a typo, would read its wall times in another zone than the one they were
 * shown in, and move it without anyone asking.
 *
 * <p>The instants still to come keep their <em>wall time</em> — a team's zone changes to correct a
 * wrong setting, and their times were typed in front of the old zone's label. The past keeps its
 * <em>instants</em>: a stage already ridden, a trip under way or a past ride is relabelled at
 * constant instant, its groups' times rewritten in the new zone so that their departures stay put.
 * An entity located by a place or a route keeps everything. Each rewritten ride or trip goes through
 * {@link PublicationEndCalculator}, the only writer of the stored end.
 */
@ApplicationScoped
public class TeamTimezoneChange {

  @Inject EntityManager entityManager;

  @Inject EventTimezoneResolver resolver;

  @Inject PublicationEndCalculator publicationEndCalculator;

  /**
   * Rewrites the team's place-less content, in the caller's transaction. Call it before the team's
   * own zone is changed.
   *
   * @return how many entities were rewritten
   */
  public int apply(Team team, ZoneId from, ZoneId to) {
    if (from.equals(to)) {
      return 0;
    }
    Instant now = Instant.now();
    // Trips whatever their stored zone: their stages may be stored in another one. A row without a
    // zone reads the team's, which is still `from` here.
    List<TeamEntity> candidates =
        entityManager
            .createQuery(
                "select te from TeamEntity te where te.team.id = :teamId and te.deleted = false"
                    + " and TYPE(te) in (Ride, Trip, Post)"
                    + " and (TYPE(te) = Trip or te.timezone is null or te.timezone = :from)",
                TeamEntity.class)
            .setParameter("teamId", team.getId())
            .setParameter("from", from.getId())
            .getResultList();
    int rewritten = 0;
    for (TeamEntity entity : candidates) {
      switch (entity) {
        case Ride ride -> {
          if (resolver.locateRide(ride.getStart(), ride.getRoute()).isEmpty()
              && rewriteRide(ride, from, to, now)) {
            publicationEndCalculator.refresh(ride);
            rewritten++;
          }
        }
        case Trip trip -> {
          Map<TripStage, Optional<ZoneId>> stages = resolver.locateStages(trip);
          boolean changed = false;
          for (Map.Entry<TripStage, Optional<ZoneId>> stage : stages.entrySet()) {
            if (stage.getValue().isEmpty() && rewrite(stage.getKey(), from, to, now)) {
              changed = true;
              rewritten++;
            }
          }
          Optional<ZoneId> located =
              resolver.locateTrip(List.copyOf(stages.values()), trip.getRoute());
          if (located.isEmpty() && rewrite(trip, from, to, now)) {
            changed = true;
            rewritten++;
          }
          if (changed) {
            publicationEndCalculator.refresh(trip);
          }
        }
        case Post post -> {
          if (rewrite(post, from, to, now)) {
            rewritten++;
          }
        }
        default -> {}
      }
    }
    return rewritten;
  }

  /**
   * A ride and its groups. Upcoming, the ride keeps its wall time and so do its groups' times; past
   * or under way, the ride keeps its instant and each group's time becomes its departure's wall
   * time in the new zone — exact as long as that departure stays on the ride's local date there.
   */
  private static boolean rewriteRide(Ride ride, ZoneId from, ZoneId to, Instant now) {
    if (!ride.zone().equals(from)) {
      return false;
    }
    boolean upcoming = !ride.getDateTime().isBefore(now);
    if (!upcoming) {
      for (RideGroup group : ride.getGroups()) {
        if (group.getTime() != null) {
          Instant start = EventTimezoneResolver.groupStart(ride.getDateTime(), group, from);
          group.setTime(start.atZone(to).toLocalTime());
        }
      }
    }
    rewrite(ride, from, to, now);
    EventTimezoneResolver.applyGroupStarts(ride, to);
    return true;
  }

  /**
   * Moves an entity stored in {@code from} to {@code to}: its instants still to come at constant
   * wall time, the past ones at constant instant. False if it is stored in another zone.
   */
  private static boolean rewrite(TeamEntity entity, ZoneId from, ZoneId to, Instant now) {
    if (!entity.zone().equals(from)) {
      return false;
    }
    if (!entity.getDateTime().isBefore(now)) {
      entity.setDateTime(EventTimezoneResolver.sameWallTime(entity.getDateTime(), from, to));
    }
    Instant publishAt = entity.getPublishAt();
    if (publishAt != null && !publishAt.isBefore(now)) {
      entity.setPublishAt(EventTimezoneResolver.sameWallTime(publishAt, from, to));
    }
    entity.setTimezone(to.getId());
    return true;
  }
}
