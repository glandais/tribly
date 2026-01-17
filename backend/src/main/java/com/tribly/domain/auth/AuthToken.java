package com.tribly.domain.auth;

import com.tribly.domain.user.User;
import com.tribly.enums.AuthTokenType;
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
@Table(name = "auth_tokens")
@NoArgsConstructor
public class AuthToken {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private @Nullable User user;

  @Column(name = "email", nullable = false, length = 250)
  private String email;

  @Column(name = "token_hash", nullable = false, unique = true, length = 100)
  private String tokenHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "token_type", nullable = false, length = 20)
  private AuthTokenType tokenType;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "used_at")
  private @Nullable Instant usedAt;

  @Column(name = "pending_display_name", length = 250)
  private @Nullable String pendingDisplayName;

  @Column(name = "pending_domain_id")
  private @Nullable Long pendingDomainId;

  public AuthToken(String email, String tokenHash, AuthTokenType tokenType, Instant expiresAt) {
    this.email = email;
    this.tokenHash = tokenHash;
    this.tokenType = tokenType;
    this.expiresAt = expiresAt;
  }

  public AuthToken(
      User user, String email, String tokenHash, AuthTokenType tokenType, Instant expiresAt) {
    this.user = user;
    this.email = email;
    this.tokenHash = tokenHash;
    this.tokenType = tokenType;
    this.expiresAt = expiresAt;
  }

  public void markUsed() {
    this.usedAt = Instant.now();
  }

  public boolean isValid() {
    return usedAt == null && expiresAt.isAfter(Instant.now());
  }
}
