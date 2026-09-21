package fr.pedalons.repository.notification;

import fr.pedalons.domain.notification.PushDevice;
import fr.pedalons.enums.PushPlatform;
import io.hypersistence.tsid.TSID;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class PushDeviceRepository implements PanacheRepository<PushDevice> {

  /**
   * Registers a token for a user, or moves and refreshes it if it is already known.
   *
   * <p>Native on purpose. The app registers at every session start <em>and</em> on every FCM token
   * refresh, and on iOS the two land together at launch: a find-then-update would have both
   * requests load the same row and the second fail its optimistic-lock check with a 500. {@code ON
   * CONFLICT (token) DO UPDATE} makes the pair — or a find-then-insert race — a single atomic
   * statement.
   */
  public void upsert(
      Long userId,
      PushPlatform platform,
      String token,
      @Nullable String deviceName,
      @Nullable String appVersion,
      Instant now) {
    getEntityManager()
        .createNativeQuery(
            """
            insert into push_devices
              (id, user_id, platform, token, device_name, app_version, created_at, last_seen_at,
               version)
            values
              (:id, :userId, :platform, :token, :deviceName, :appVersion, :now, :now, 0)
            on conflict (token) do update set
              user_id = excluded.user_id,
              platform = excluded.platform,
              device_name = excluded.device_name,
              app_version = excluded.app_version,
              last_seen_at = excluded.last_seen_at,
              version = push_devices.version + 1
            """)
        .setParameter("id", TSID.Factory.getTsid().toLong())
        .setParameter("userId", userId)
        .setParameter("platform", platform.name())
        .setParameter("token", token)
        .setParameter("deviceName", deviceName)
        .setParameter("appVersion", appVersion)
        .setParameter("now", Timestamp.from(now))
        .executeUpdate();
  }

  /** The devices to push to, oldest first so the order of a member's devices is stable. */
  public List<PushDevice> findByUser(Long userId) {
    return list("user.id = ?1 order by createdAt", userId);
  }

  /** Unregistering is scoped to the caller: someone else's token is not theirs to remove. */
  public long deleteByUserAndToken(Long userId, String token) {
    return delete("user.id = ?1 and token = ?2", userId, token);
  }

  /**
   * Drops the tokens FCM has told us are dead. Not scoped to a user on purpose: the answer is about
   * the token, and by then it may already have moved to another account on the same phone —
   * a token FCM rejects is unreachable whoever holds it.
   */
  public long deleteByTokens(List<String> tokens) {
    return tokens.isEmpty() ? 0 : delete("token in ?1", tokens);
  }
}
