package com.tribly.domain.ride;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RideRepository implements PanacheRepository<Ride> {

    public Optional<Ride> findByIdAndTeam(Long rideId, Long teamId) {
        return find("id = ?1 and team.id = ?2 and deleted = false", rideId, teamId).firstResultOptional();
    }

    public List<Ride> findByTeam(Long teamId, int page, int size) {
        return find("team.id = ?1 and deleted = false order by date desc", teamId)
                .page(page, size)
                .list();
    }

    public List<Ride> findByTeamAndDateRange(Long teamId, LocalDate from, LocalDate to, int page, int size) {
        return find("team.id = ?1 and deleted = false and date >= ?2 and date <= ?3 order by date",
                teamId, from, to)
                .page(page, size)
                .list();
    }

    public List<Ride> findByTeamAndStatus(Long teamId, RideStatus status, int page, int size) {
        return find("team.id = ?1 and status = ?2 and deleted = false order by date desc",
                teamId, status)
                .page(page, size)
                .list();
    }

    public List<Ride> findUpcomingByTeam(Long teamId, int page, int size) {
        return find("team.id = ?1 and deleted = false and status = ?2 and date >= ?3 order by date",
                teamId, RideStatus.PUBLISHED, LocalDate.now())
                .page(page, size)
                .list();
    }

    public long countByTeam(Long teamId) {
        return count("team.id = ?1 and deleted = false", teamId);
    }

    public long countByTeamAndStatus(Long teamId, RideStatus status) {
        return count("team.id = ?1 and status = ?2 and deleted = false", teamId, status);
    }

    public Optional<Ride> findByTeamAndSlug(Long teamId, String slug) {
        return find("team.id = ?1 and slug = ?2 and deleted = false", teamId, slug).firstResultOptional();
    }

    public boolean existsByTeamAndSlug(Long teamId, String slug) {
        return count("team.id = ?1 and slug = ?2 and deleted = false", teamId, slug) > 0;
    }

    /**
     * Find rides that should be auto-published (DRAFT status with publishAt in the past).
     */
    public List<Ride> findRidesToAutoPublish(RideStatus status, Instant now) {
        return find("status = ?1 and publishAt is not null and publishAt <= ?2 and deleted = false",
                status, now).list();
    }
}
