package fr.pedalons.domain.calendar;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "calendar_tokens")
@NoArgsConstructor
public class CalendarToken extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(name = "token", nullable = false, unique = true, length = 64)
  private String token;

  /**
   * Last time a calendar app fetched the feed, to the day (docs/LEDGER_*.md SEC-17). Null for a
   * token not fetched since it was created: its creation date stands in.
   */
  @Column(name = "last_used_at")
  private @Nullable Instant lastUsedAt;

  public CalendarToken(User user, String token) {
    super(user);
    this.user = user;
    this.token = token;
  }

  /** When the token last showed a sign of life: its last fetch, or else its creation. */
  public Instant lastActivity() {
    return lastUsedAt != null ? lastUsedAt : getCreatedAt();
  }
}
