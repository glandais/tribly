package com.tribly.domain.common.repository;

import com.tribly.domain.common.Publication;
import com.tribly.enums.Status;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public interface PublicationRepository<T extends Publication> extends BaseRepository<T> {

  default TriblyPage<T> find(PublicationQuery rideQuery) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", rideQuery.teamId()))
            .and("deleted = false", Map.of())
            .and("status in :statuses", Map.of("statuses", rideQuery.statuses()))
            .order("dateTime desc");
    if (rideQuery.slug() != null) {
      triblyQuery = triblyQuery.and("slug = :slug", Map.of("slug", rideQuery.slug()));
    }
    if (rideQuery.from() != null) {
      triblyQuery = triblyQuery.and("dateTime >= :from", Map.of("from", rideQuery.from()));
    }
    if (rideQuery.to() != null) {
      triblyQuery = triblyQuery.and("dateTime <= :to", Map.of("to", rideQuery.to()));
    }
    if (rideQuery.visibility() != null) {
      triblyQuery =
          triblyQuery.and("visibility = :visibility", Map.of("visibility", rideQuery.visibility()));
    }

    return getPage(triblyQuery, rideQuery);
  }

  /**
   * Find rides that should be auto-published (DRAFT status with publishAt in the past).
   */
  default List<T> findPublicationsToAutoPublish(Status status, Instant now) {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            status,
            now)
        .list();
  }
}
