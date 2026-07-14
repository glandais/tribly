package fr.pedalons.domain.social;

import fr.pedalons.domain.user.User;
import fr.pedalons.enums.SocialProvider;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * Binds a tribly {@link User} to an external identity provider account (e.g. a Strava athlete id).
 * Identity only — no OAuth tokens are stored, since login just needs the external id. Unique per
 * {@code (domain_id, provider, external_user_id)} so the same Strava athlete maps to at most one
 * user per domain.
 */
@Setter
@Getter
@Entity
@Table(
    name = "user_social_identities",
    indexes = {@Index(columnList = "user_id")},
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_social_identity",
          columnNames = {"domain_id", "provider", "external_user_id"})
    })
@NoArgsConstructor
public class UserSocialIdentity {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false, length = 20)
  private SocialProvider provider;

  @Column(name = "external_user_id", nullable = false, length = 100)
  private String externalUserId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  @Column(name = "last_login_at")
  private @Nullable Instant lastLoginAt;

  public UserSocialIdentity(
      User user, Long domainId, SocialProvider provider, String externalUserId) {
    this.user = user;
    this.domainId = domainId;
    this.provider = provider;
    this.externalUserId = externalUserId;
  }

  public void recordLogin() {
    this.lastLoginAt = Instant.now();
  }
}
