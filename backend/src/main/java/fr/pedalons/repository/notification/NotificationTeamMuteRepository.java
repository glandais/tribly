package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.NotificationTeamMute;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@ApplicationScoped
public class NotificationTeamMuteRepository implements PanacheRepository<NotificationTeamMute> {

  /** Keeps each {@code IN} list well below PostgreSQL's bind-parameter ceiling. */
  private static final int CHUNK = 1000;

  public List<NotificationTeamMute> findByUser(Long userId) {
    return list("user.id", userId);
  }

  public Optional<NotificationTeamMute> findByUserAndTeam(Long userId, Long teamId) {
    return find("user.id = ?1 and team.id = ?2", userId, teamId).firstResultOptional();
  }

  /** Who, in an audience, muted this team — one query per thousand members. */
  public Set<Long> findMutedUserIds(Long teamId, List<Long> userIds) {
    Set<Long> muted = new HashSet<>();
    for (int from = 0; from < userIds.size(); from += CHUNK) {
      List<Long> chunk = userIds.subList(from, Math.min(from + CHUNK, userIds.size()));
      muted.addAll(
          getEntityManager()
              .createQuery(
                  "select m.user.id from NotificationTeamMute m"
                      + " where m.team.id = :teamId and m.user.id in :userIds",
                  Long.class)
              .setParameter("teamId", teamId)
              .setParameter("userIds", chunk)
              .getResultList());
    }
    return muted;
  }

  public long deleteByUser(Long userId) {
    return delete("user.id", userId);
  }
}
