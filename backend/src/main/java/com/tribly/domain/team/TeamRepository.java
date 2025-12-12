package com.tribly.domain.team;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class TeamRepository implements PanacheRepository<Team> {

    public Optional<Team> findBySlug(String slug) {
        return find("slug = ?1 and deleted = false", slug).firstResultOptional();
    }

    public Optional<Team> findActiveById(Long id) {
        return find("id = ?1 and deleted = false", id).firstResultOptional();
    }

    public boolean existsBySlug(String slug) {
        return count("slug = ?1 and deleted = false", slug) > 0;
    }

    public List<Team> findPublicTeams(int page, int size) {
        return find("isPublic = true and deleted = false", Sort.by("name"))
                .page(Page.of(page, size))
                .list();
    }

    public long countPublicTeams() {
        return count("isPublic = true and deleted = false");
    }

    public List<Team> searchPublicTeams(String query, int page, int size) {
        return find("isPublic = true and deleted = false and (lower(name) like ?1 or lower(description) like ?1)",
                Sort.by("name"),
                "%" + query.toLowerCase() + "%")
                .page(Page.of(page, size))
                .list();
    }

    public List<Team> findByUserId(Long userId) {
        return getEntityManager()
                .createQuery(
                        "SELECT t FROM Team t " +
                                "JOIN t.members m " +
                                "WHERE m.user.id = :userId AND t.deleted = false AND m.deleted = false " +
                                "ORDER BY t.name",
                        Team.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
