package com.tribly.domain.user;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

  public User(String email, String displayName) {
    super(null);
    this.email = email;
    this.displayName = displayName;
    setCreatedBy(this);
  }

  public void recordLogin() {
    this.lastLoginAt = Instant.now();
  }
}
