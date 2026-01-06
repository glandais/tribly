package com.tribly.domain.team.repository;

import com.tribly.domain.common.query.TriblyQuery;
import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.UserTeam;
import com.tribly.enums.TeamRole;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserTeamRepository implements BaseRepository<UserTeam> {

  public Optional<UserTeam> findByUserAndTeamIncludingDeleted(Long userId, Long teamId) {
    return find("user.id = ?1 and team.id = ?2", userId, teamId).firstResultOptional();
  }

  public TriblyPage<UserTeam> findByTeam(Long teamId, int page, int size) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.id = :teamId", Map.of("teamId", teamId))
            .and("deleted = false", Map.of());
    return getPage(triblyQuery, page, size);
  }

  public long countAdminsByTeam(Long teamId) {
    return count("team.id = ?1 and role = ?2 and deleted = false", teamId, TeamRole.ADMIN);
  }

  public Optional<UserTeam> findByUserAndTeam(Long userId, Long teamId) {
    return getEntityManager()
        .createQuery(
            "SELECT ut FROM UserTeam ut "
                + "JOIN ut.team t "
                + "WHERE ut.user.id = :userId AND t.id = :teamId "
                + "AND ut.deleted = false AND t.deleted = false",
            UserTeam.class)
        .setParameter("userId", userId)
        .setParameter("teamId", teamId)
        .getResultStream()
        .findFirst();
  }
}
