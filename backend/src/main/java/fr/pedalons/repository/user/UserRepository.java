package fr.pedalons.repository.user;

import fr.pedalons.domain.user.User;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserRepository implements BaseRepository<User> {

  public Optional<User> findByEmailAndDomain(Long domainId, String email) {
    return find("domain.id = ?1 and email = ?2 and deleted = false", domainId, email)
        .firstResultOptional();
  }

  public Optional<User> findActiveByIdAndDomain(Long domainId, Long id) {
    return find("domain.id = ?1 and id = ?2 and deleted = false", domainId, id)
        .firstResultOptional();
  }

  public Optional<User> findActiveById(Long id) {
    return find("id = ?1 and deleted = false", id).firstResultOptional();
  }

  /**
   * Migrated Strava accounts carry a synthesized {@code strava_<athleteId>@<placeholderDomain>}
   * email. Used by the social-identity backfill. The {@code _} in {@code strava_} is escaped so it
   * is matched literally rather than as a LIKE wildcard.
   */
  public List<User> findPlaceholderStravaUsers(Long domainId, String placeholderDomain) {
    return find(
            "domain.id = ?1 and email like ?2 escape '!' and deleted = false",
            domainId,
            "strava!_%@" + placeholderDomain.toLowerCase())
        .list();
  }

  public List<User> searchByDisplayNameAndDomain(Long domainId, String query, int limit) {
    return find(
            "domain.id = ?1 and LOWER(displayName) LIKE LOWER(?2) and deleted = false",
            domainId,
            "%" + query + "%")
        .page(0, limit)
        .list();
  }

  /**
   * The same search, narrowed to the members of one team.
   *
   * <p>Strictly a subset of {@link #searchByDisplayNameAndDomain}: it can only return fewer users,
   * never a user the unfiltered search would have hidden. It exists so a picker that must yield a
   * team member — designating a ride group's leader — cannot offer someone the server will then
   * reject.
   */
  public List<User> searchByDisplayNameAndTeam(
      Long domainId, Long teamId, String query, int limit) {
    return find(
            "domain.id = ?1 and LOWER(displayName) LIKE LOWER(?2) and deleted = false"
                + " and id in (select ut.user.id from UserTeam ut where ut.team.id = ?3)",
            domainId,
            "%" + query + "%",
            teamId)
        .page(0, limit)
        .list();
  }
}
