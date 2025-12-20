package com.tribly.domain.team.repository;

import com.tribly.domain.common.repository.BaseRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.common.repository.TriblyQuery;
import com.tribly.domain.team.UserTeam;
import com.tribly.enums.TeamRole;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class UserTeamRepository implements BaseRepository<UserTeam> {

  public Optional<UserTeam> findByUserAndTeamIncludingDeleted(Long userId, String teamSlug) {
    return find("user.id = ?1 and team.slug = ?2", userId, teamSlug).firstResultOptional();
  }

  public TriblyPage<UserTeam> findByTeam(String slug, int page, int size) {
    TriblyQuery triblyQuery =
        new TriblyQuery()
            .and("team.slug = :slug", Map.of("slug", slug))
            .and("deleted = false", Map.of());
    return getPage(triblyQuery, page, size);
  }

  public long countAdminsByTeam(String slug) {
    return count("team.slug = ?1 and role = ?2 and deleted = false", slug, TeamRole.ADMIN);
  }

  public Optional<UserTeam> findByUserAndTeam(Long userId, String teamSlug) {
    return getEntityManager()
        .createQuery(
            "SELECT ut FROM UserTeam ut "
                + "JOIN ut.team t "
                + "WHERE ut.user.id = :userId AND t.slug = :teamSlug "
                + "AND ut.deleted = false AND t.deleted = false",
            UserTeam.class)
        .setParameter("userId", userId)
        .setParameter("teamSlug", teamSlug)
        .getResultStream()
        .findFirst();
  }
}
