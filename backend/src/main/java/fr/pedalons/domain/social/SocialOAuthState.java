package fr.pedalons.domain.social;

import fr.pedalons.domain.user.User;
import fr.pedalons.enums.SocialAuthPurpose;
import fr.pedalons.enums.SocialProvider;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * CSRF state for a social OAuth flow. Mirrors {@code GpsOAuthState} but the user is nullable (a
 * LOGIN flow starts with no authenticated user) and it carries the flow {@code purpose} plus the
 * origin tenant base URL so the callback can hand the session back to the right host.
 */
@Setter
@Getter
@Entity
@Table(name = "social_oauth_states")
@NoArgsConstructor
public class SocialOAuthState {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private @Nullable User user;

  @Column(name = "state", nullable = false, unique = true, length = 100)
  private String state;

  @Enumerated(EnumType.STRING)
  @Column(name = "provider", nullable = false, length = 20)
  private SocialProvider provider;

  @Enumerated(EnumType.STRING)
  @Column(name = "purpose", nullable = false, length = 10)
  private SocialAuthPurpose purpose;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "redirect_uri", nullable = false, length = 500)
  private String redirectUri;

  @Column(name = "return_base_url", nullable = false, length = 500)
  private String returnBaseUrl;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt = Instant.now();

  public SocialOAuthState(
      @Nullable User user,
      String state,
      SocialProvider provider,
      SocialAuthPurpose purpose,
      Instant expiresAt,
      String redirectUri,
      String returnBaseUrl,
      Long domainId) {
    this.user = user;
    this.state = state;
    this.provider = provider;
    this.purpose = purpose;
    this.expiresAt = expiresAt;
    this.redirectUri = redirectUri;
    this.returnBaseUrl = returnBaseUrl;
    this.domainId = domainId;
  }

  public boolean isValid() {
    return expiresAt.isAfter(Instant.now());
  }
}
