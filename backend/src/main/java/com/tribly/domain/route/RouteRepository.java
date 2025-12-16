package com.tribly.domain.route;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RouteRepository implements PanacheRepository<Route> {

    /**
     * Find a route by ID within a specific team.
     */
    public Optional<Route> findByIdAndTeam(Long routeId, Long teamId) {
        return find("id = ?1 and team.id = ?2 and deleted = false", routeId, teamId)
                .firstResultOptional();
    }

    /**
     * Find an active (non-deleted) route by ID.
     */
    public Optional<Route> findActiveById(Long id) {
        return find("id = ?1 and deleted = false", id).firstResultOptional();
    }

    /**
     * Find all routes for a team with pagination.
     */
    public List<Route> findByTeam(Long teamId, int page, int size) {
        return find("team.id = ?1 and deleted = false", Sort.by("createdAt").descending(), teamId)
                .page(Page.of(page, size))
                .list();
    }

    /**
     * Count total routes for a team.
     */
    public long countByTeam(Long teamId) {
        return count("team.id = ?1 and deleted = false", teamId);
    }

    /**
     * Find public routes for a team.
     */
    public List<Route> findPublicByTeam(Long teamId, int page, int size) {
        return find("team.id = ?1 and isPublic = true and deleted = false",
                Sort.by("createdAt").descending(), teamId)
                .page(Page.of(page, size))
                .list();
    }

    /**
     * Count public routes for a team.
     */
    public long countPublicByTeam(Long teamId) {
        return count("team.id = ?1 and isPublic = true and deleted = false", teamId);
    }
}
