package com.tribly.domain.ride.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.common.repository.TriblyQuery;
import com.tribly.domain.ride.Ride;
import com.tribly.enums.RideStatus;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class RideRepository implements BaseRepository<Ride> {

  public TriblyPage<Ride> find(RideQuery rideQuery) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", rideQuery.teamId()))
            .and("deleted = false", Map.of())
            .and("status in :statuses", Map.of("statuses", rideQuery.statuses()))
            .order("date desc");
    if (rideQuery.slug() != null) {
      triblyQuery = triblyQuery.and("slug = :slug", Map.of("slug", rideQuery.slug()));
    }
    if (rideQuery.from() != null) {
      triblyQuery = triblyQuery.and("date >= :from", Map.of("from", rideQuery.from()));
    }
    if (rideQuery.to() != null) {
      triblyQuery = triblyQuery.and("date <= :to", Map.of("to", rideQuery.to()));
    }
    if (rideQuery.visibility() != null) {
      triblyQuery =
          triblyQuery.and("visibility = :visibility", Map.of("visibility", rideQuery.visibility()));
    }

    return getPage(triblyQuery, rideQuery);
  }

  public boolean existsByTeamAndSlug(Long teamId, String slug) {
    return count("team.id = ?1 and slug = ?2 and deleted = false", teamId, slug) > 0;
  }

  /**
   * Find rides that should be auto-published (DRAFT status with publishAt in the past).
   */
  public List<Ride> findRidesToAutoPublish(RideStatus status, Instant now) {
    return find(
            "status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
            status,
            now)
        .list();
  }
}
