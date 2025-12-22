package com.tribly.domain.user;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

  @NotBlank
  @Email
  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @NotBlank
  @Size(max = 255)
  @Column(name = "display_name", nullable = false)
  private String displayName;

  @Column(name = "avatar_url", length = 500)
  private @Nullable String avatarUrl;

  @Column(name = "last_login_at")
  private @Nullable Instant lastLoginAt;

  public User() {}

  public User(String email, String displayName) {
    this.email = email;
    this.displayName = displayName;
  }

  public void recordLogin() {
    this.lastLoginAt = Instant.now();
  }
}
