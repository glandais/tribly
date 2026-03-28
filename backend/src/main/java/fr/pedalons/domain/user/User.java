package fr.pedalons.domain.user;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.enums.PlatformRole;
import fr.pedalons.enums.UnitSystem;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "users",
    indexes = {@Index(columnList = "domain_id, deleted")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_users_domain_email",
          columnNames = {"domain_id", "email"})
    })
@NoArgsConstructor
public class User extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "domain_id", nullable = false)
  private Domain domain;

  @Column(name = "email", nullable = false, length = 250)
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

  @Enumerated(EnumType.STRING)
  @Column(name = "platform_role", length = 20)
  private @Nullable PlatformRole platformRole;

  @Column(name = "deleted", nullable = false)
  private boolean deleted = false;

  public User(Domain domain, String email, String displayName) {
    super(null);
    this.domain = domain;
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
