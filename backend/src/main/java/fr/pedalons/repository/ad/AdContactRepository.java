package fr.pedalons.repository.ad;

import fr.pedalons.domain.ad.AdContact;
import fr.pedalons.repository.common.BaseRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@ApplicationScoped
public class AdContactRepository implements BaseRepository<AdContact> {

  /**
   * How many messages this sender has relayed since {@code minutes} ago, across every ad and every
   * team.
   *
   * <p>The window is per sender rather than per ad on purpose. Limiting per ad would leave the
   * obvious hole open: a member who may read a team's classifieds may write to all of them, so
   * "three per ad" on twenty ads is sixty relayed emails from one account. What the limit protects
   * is the mail reputation of the domain, and that is consumed by the sender whatever they write
   * to.
   *
   * <p>No domain clause: {@code createdBy} is a user, and a user belongs to exactly one domain, so
   * the sender id already scopes the count. Counting across the sender's own domain boundary is not
   * possible.
   */
  public long countRecentBySender(Long senderId, int minutes) {
    Instant cutoff = Instant.now().minus(minutes, ChronoUnit.MINUTES);
    return count("createdBy.id = ?1 and createdAt > ?2", senderId, cutoff);
  }
}
