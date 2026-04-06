package fr.pedalons.repository.team;

import fr.pedalons.domain.team.UserTeam;
import fr.pedalons.dto.common.PedalonsPage;
import fr.pedalons.enums.TeamRole;
import fr.pedalons.repository.common.BaseRepository;
import fr.pedalons.repository.query.PedalonsQuery;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserTeamRepository implements BaseRepository<UserTeam> {

  public List<UserTeam> findByUserId(Long userId) {
    return find("user.id = ?1 and user.deleted = false and team.deleted = false", userId).list();
  }

  public PedalonsPage<UserTeam> findByTeam(Long teamId, int page, int size) {
    PedalonsQuery pedalonsQuery =
        new PedalonsQuery()
            .and("team.id = :teamId", Map.of("teamId", teamId))
            .and("user.deleted = false", Map.of())
            .and("team.deleted = false", Map.of());
    return getPage(pedalonsQuery, page, size);
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
}
