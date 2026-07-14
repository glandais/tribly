package fr.pedalons.domain.social;

import fr.pedalons.domain.user.User;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * Short-lived, single-use hand-off code minted by the (unauthenticated) social OAuth callback and
 * exchanged by the SPA on its own host for a real session. Stored hashed; scoped to a user + domain;
 * carries whether the account still needs a real email.
 */
@Setter
@Getter
@Entity
@Table(name = "social_login_codes")
@NoArgsConstructor
public class SocialLoginCode {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Column(name = "code_hash", nullable = false, unique = true, length = 100)
  private String codeHash;

  @Column(name = "needs_email", nullable = false)
  private boolean needsEmail;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "used_at")
  private @Nullable Instant usedAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public SocialLoginCode(
      User user, Long domainId, String codeHash, boolean needsEmail, Instant expiresAt) {
    this.user = user;
    this.domainId = domainId;
    this.codeHash = codeHash;
    this.needsEmail = needsEmail;
    this.expiresAt = expiresAt;
  }

  public boolean isValid() {
    return usedAt == null && expiresAt.isAfter(Instant.now());
  }

  public void markUsed() {
    this.usedAt = Instant.now();
  }
}
