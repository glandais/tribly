package com.tribly.domain.user;

import com.tribly.domain.common.BaseEntity;
import com.tribly.enums.UnitSystem;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {

  @Column(name = "email", nullable = false, unique = true, length = 250)
  private String email;

  @Column(name = "display_name", nullable = false, length = 250)
  private String displayName;

  @Column(name = "avatar_url", length = 500)
  private @Nullable String avatarUrl;

  @Column(name = "last_login_at")
  private @Nullable Instant lastLoginAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "unit_system", length = 10)
  private @Nullable UnitSystem unitSystem;

  @Column(name = "email_verified", nullable = false)
  private boolean emailVerified = false;

  @Column(name = "email_verified_at")
  private @Nullable Instant emailVerifiedAt;

  public User(String email, String displayName) {
    super(null);
    this.email = email;
    this.displayName = displayName;
    setCreatedBy(this);
  }

  public void recordLogin() {
    this.lastLoginAt = Instant.now();
  }

  public void markEmailVerified() {
    this.emailVerified = true;
    this.emailVerifiedAt = Instant.now();
  }
}
