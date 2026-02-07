package com.tribly.domain.gps;

import com.tribly.domain.user.User;
import com.tribly.enums.GpsServiceType;
import io.hypersistence.utils.hibernate.id.Tsid;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "gps_service_connections")
@NoArgsConstructor
public class GpsServiceConnection {

  @Id @Tsid private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "service_type", nullable = false, length = 20)
  private GpsServiceType serviceType;

  @Column(name = "access_token_encrypted", nullable = false, columnDefinition = "bytea")
  private byte[] accessTokenEncrypted;

  @Column(name = "refresh_token_encrypted", columnDefinition = "bytea")
  private byte @Nullable [] refreshTokenEncrypted;

  @Column(name = "token_expires_at")
  private @Nullable Instant tokenExpiresAt;

  @Column(name = "external_user_id", length = 100)
  private @Nullable String externalUserId;

  @Column(name = "connected_at", nullable = false)
  private Instant connectedAt;

  @Column(name = "last_used_at")
  private @Nullable Instant lastUsedAt;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  @Version private Long version;

  public GpsServiceConnection(User user, GpsServiceType serviceType) {
    this.user = user;
    this.serviceType = serviceType;
    this.connectedAt = Instant.now();
  }

  public boolean isTokenExpired() {
    if (tokenExpiresAt == null) {
      return false;
    }
    // Consider expired if less than 5 minutes remaining
    return Instant.now().plusSeconds(300).isAfter(tokenExpiresAt);
  }

  public void markUsed() {
    this.lastUsedAt = Instant.now();
  }
}
