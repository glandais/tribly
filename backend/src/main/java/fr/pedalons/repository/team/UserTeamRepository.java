package fr.pedalons.repository.team;

import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.OrClause;
import fr.pedalons.repository.query.PedalonsQuery;
import fr.pedalons.repository.query.SimpleClause;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class UserTeamRepository implements BaseRepository<UserTeam> {

  public List<UserTeam> findByUserId(Long userId) {
    return find("user.id = ?1 and user.deleted = false and team.deleted = false", userId).list();
  }

  public PedalonsPage<UserTeam> findByTeam(
      Long teamId, int page, int size, String search, TeamRole role) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery()
            .and("team.id = :teamId", Map.of("teamId", teamId))
            .and("user.deleted = false", Map.of())
            .and("team.deleted = false", Map.of());
    if (search != null && !search.isBlank()) {
      String searchParam = "%" + search.toLowerCase() + "%";
      OrClause orClause = new OrClause();
      orClause.add(
          new SimpleClause("LOWER(user.displayName) LIKE :search", Map.of("search", searchParam)));
      orClause.add(
          new SimpleClause("LOWER(user.email) LIKE :search", Map.of("search", searchParam)));
      pedalonsQuery.and(orClause);
    }
    if (role != null) {
      pedalonsQuery.and("role = :role", Map.of("role", role));
    }
    return getPage(pedalonsQuery, page, size);
  }

  /**
   * Returns the number of non-deleted teams where the given user holds an ADMIN role within the
   * specified domain. Used to enforce the {@code MAX_ADMIN_TEAMS_PER_USER} creation limit for
   * non-platform-admin users.
   */
  public long countAdminTeamsByUserAndDomain(Long userId, Long domainId) {
    return getEntityManager()
        .createQuery(
            "SELECT COUNT(ut) FROM UserTeam ut "
                + "JOIN ut.team t "
                + "WHERE ut.user.id = :userId AND t.domain.id = :domainId "
                + "AND ut.role = :role "
                + "AND t.deleted = false",
            Long.class)
        .setParameter("userId", userId)
        .setParameter("domainId", domainId)
        .setParameter("role", TeamRole.ADMIN)
        .getSingleResult();
  }

  public long countAdminsByTeam(Long teamId) {
    return count(
        "team.id = ?1 and role = ?2 and " + "team.deleted = false and user.deleted = false",
        teamId,
        TeamRole.ADMIN);
  }

  public Optional<UserTeam> findByUserAndTeam(Long userId, Long teamId) {
    return getEntityManager()
        .createQuery(
            "SELECT ut FROM UserTeam ut "
                + "JOIN ut.team t "
                + "JOIN ut.user u "
                + "WHERE ut.user.id = :userId AND t.id = :teamId "
                + "AND t.deleted = false "
                + "AND u.deleted = false",
            UserTeam.class)
        .setParameter("userId", userId)
        .setParameter("teamId", teamId)
        .getResultStream()
        .findFirst();
  }

  /**
   * Which of these teams the user is a member of — one query for a whole page.
   *
   * <p>Same rule as {@link #findByUserAndTeam} (any role counts, neither side soft-deleted), asked
   * for many teams at once. A page of publications spans a handful of teams at most, so a per-row
   * {@code findByUserAndTeam} would be a per-row round-trip for an answer that repeats.
   *
   * @param domainId re-stated rather than inferred from the ids: this is the clause that keeps a
   *     membership in one domain from vouching for a team in another
   */
  public Set<Long> findMemberTeamIds(Long userId, Long domainId, Collection<Long> teamIds) {
    if (teamIds.isEmpty()) {
      return Set.of();
    }
    return new HashSet<>(
        getEntityManager()
            .createQuery(
                "SELECT t.id FROM UserTeam ut "
                    + "JOIN ut.team t "
                    + "JOIN ut.user u "
                    + "WHERE ut.user.id = :userId AND t.id IN (:teamIds) "
                    + "AND t.domain.id = :domainId "
                    + "AND t.deleted = false "
                    + "AND u.deleted = false",
                Long.class)
            .setParameter("userId", userId)
            .setParameter("teamIds", teamIds)
            .setParameter("domainId", domainId)
            .getResultList());
  }
}
