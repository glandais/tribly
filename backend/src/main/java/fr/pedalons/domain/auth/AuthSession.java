package fr.pedalons.domain.auth;

import fr.pedalons.domain.user.User;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "auth_sessions")
@NoArgsConstructor
public class AuthSession {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "refresh_token_hash", nullable = false, length = 100)
  private String refreshTokenHash;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "last_used_at")
  private @Nullable Instant lastUsedAt;

  @Column(name = "user_agent", length = 500)
  private @Nullable String userAgent;

  @Column(name = "ip_address", length = 45)
  private @Nullable String ipAddress;

  @Column(name = "revoked", nullable = false)
  private boolean revoked = false;

  @Column(name = "revoked_at")
  private @Nullable Instant revokedAt;

  public AuthSession(User user, String refreshTokenHash, Instant expiresAt) {
    this.user = user;
    this.refreshTokenHash = refreshTokenHash;
    this.expiresAt = expiresAt;
  }

  public void markUsed() {
    this.lastUsedAt = Instant.now();
  }

  public void revoke() {
    this.revoked = true;
    this.revokedAt = Instant.now();
  }

  public boolean isValid() {
    return !revoked && expiresAt.isAfter(Instant.now());
  }
}
