package fr.pedalons.repository.common;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.dto.publications.response.PublicationType;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Status;
import fr.pedalons.enums.TeamEntityType;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AllPublicationRepository
    implements TeamEntityRepository<Publication, PublicationQuery> {
  @Override
  public TeamEntityType getEntityType() {
    return TeamEntityType.PUBLICATION;
  }

  @Override
  public EntityType getAllEntityType() {
    return EntityType.PUBLICATION;
  }

  /**
   * Find publications that should be auto-published (DRAFT status with publishAt in the past).
   */
  public List<Publication> findPublicationsToAutoPublish() {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            Status.DRAFT,
            Instant.now())
        .list();
  }

  /**
   * Correlated EXISTS over both participation tables — a ride is joined through its groups, a trip
   * directly. Written as an EXISTS rather than a join so a publication is never duplicated and the
   * page count stays right; {@code idx_ride_participations_user_group (user_id, ride_group_id)} and
   * {@code idx_trip_participations_user_trip (user_id, trip_id)} both lead on {@code user_id}, which
   * is the selective column here.
   */
  private static final String PARTICIPATING_CLAUSE =
      "(exists (select 1 from RideParticipation rp "
          + "where rp.rideGroup.ride.id = te.id and rp.user.id = :participatingUserId) "
          + "or exists (select 1 from TripParticipation tp "
          + "where tp.trip.id = te.id and tp.user.id = :participatingUserId))";

  @Override
  public PedalonsQuery andSpecific(PedalonsQuery pedalonsQuery, PublicationQuery query) {
    PublicationType publicationType = query.type();
    if (publicationType != null) {
      pedalonsQuery =
          pedalonsQuery.and("TYPE(te) = :type", Map.of("type", publicationType.getType()));
    }
    Status status = query.status();
    if (status != null) {
      // ANDed with the visibility rules already in place: a status filter narrows what the caller
      // may see, it never unlocks a status they are not allowed to see.
      pedalonsQuery =
          pedalonsQuery.and("te.status = :statusFilter", Map.of("statusFilter", status));
    }
    if (query.participating()) {
      Long userId = query.userId();
      if (userId == null) {
        // Nobody is registered when nobody is logged in. Same rule as minRole: yield nothing rather
        // than ignore the filter.
        pedalonsQuery = pedalonsQuery.and("te.id IS NULL", Map.of());
      } else {
        pedalonsQuery =
            pedalonsQuery.and(PARTICIPATING_CLAUSE, Map.of("participatingUserId", userId));
      }
    }
    if (query.ascending()) {
      // getPedalonsQuery set the default ordering before calling us; order() replaces it.
      pedalonsQuery = pedalonsQuery.order("dateTime asc");
    }
    return pedalonsQuery;
  }

  @Override
  public PublicationQuery getQuerySlug(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      String slug,
      boolean includeDeleted,
      boolean platformAdmin) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .slug(slug)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  @Override
  public PublicationQuery getQueryId(
      Long domainId,
      Long teamId,
      @Nullable Long userId,
      Long id,
      boolean includeDeleted,
      boolean platformAdmin) {
    return PublicationQuery.builder()
        .domainId(domainId)
        .teamIds(Set.of(teamId))
        .userId(userId)
        .id(id)
        .includeDeleted(includeDeleted)
        .platformAdmin(platformAdmin)
        .build();
  }

  /**
   * Rides, posts and trips a user authored, for the GDPR data export. Single-table inheritance means
   * one query covers all three.
   */
  public List<Publication> findByCreator(Long domainId, Long userId) {
    return list("createdBy.id = ?2 and team.domain.id = ?1 order by createdAt", domainId, userId);
  }
}
