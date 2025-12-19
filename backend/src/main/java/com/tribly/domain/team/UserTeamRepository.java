package com.tribly.domain.team;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserTeamRepository implements PanacheRepository<UserTeam> {

    private static final Logger LOG = Logger.getLogger(UserTeamRepository.class);

    public Optional<UserTeam> findByUserAndTeam(Long userId, Long teamId) {
        return find("user.id = ?1 and team.id = ?2 and deleted = false", userId, teamId)
                .firstResultOptional();
    }

    public Optional<UserTeam> findByUserAndTeamIncludingDeleted(Long userId, Long teamId) {
        return find("user.id = ?1 and team.id = ?2", userId, teamId)
                .firstResultOptional();
    }

    public boolean existsByUserAndTeam(Long userId, Long teamId) {
        return count("user.id = ?1 and team.id = ?2 and deleted = false", userId, teamId) > 0;
    }

    public List<UserTeam> findByTeam(Long teamId, int page, int size) {
        return find("team.id = ?1 and deleted = false", teamId)
                .page(Page.of(page, size))
                .list();
    }

    public long countByTeam(Long teamId) {
        return count("team.id = ?1 and deleted = false", teamId);
    }

    public long countAdminsByTeam(Long teamId) {
        return count("team.id = ?1 and role = ?2 and deleted = false", teamId, TeamRole.ADMIN);
    }

    public Optional<UserTeam> findByUserAndTeamSlug(Long userId, String teamSlug) {
        LOG.infov("findByUserAndTeamSlug: Querying for userId={0}, teamSlug={1}", userId, teamSlug);

        // Debug: Count all UserTeam records for this user
        long userTeamCount = count("user.id = ?1 and deleted = false", userId);
        LOG.infov("findByUserAndTeamSlug: User {0} has {1} total team memberships", userId, userTeamCount);

        Optional<UserTeam> result = getEntityManager()
                .createQuery(
                        "SELECT ut FROM UserTeam ut " +
                                "JOIN ut.team t " +
                                "WHERE ut.user.id = :userId AND t.slug = :teamSlug " +
                                "AND ut.deleted = false AND t.deleted = false",
                        UserTeam.class)
                .setParameter("userId", userId)
                .setParameter("teamSlug", teamSlug)
                .getResultStream()
                .findFirst();

        LOG.infov("findByUserAndTeamSlug: Query result present={0}", result.isPresent());
        return result;
    }
}
