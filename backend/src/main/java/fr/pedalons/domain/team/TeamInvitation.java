package fr.pedalons.domain.team;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.InvitationStatus;
import fr.pedalons.enums.TeamRole;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * An offer to join a team, addressed to an e-mail rather than to an account.
 *
 * <p>Its own table rather than a fifth {@code AuthTokenType}, for a reason that only shows up later:
 * {@code AuthCleanupScheduler} deletes auth tokens once they are used or expired, so every accepted
 * and every revoked invitation would vanish within a day — and who let whom into a team is exactly
 * the kind of thing one wants to still be able to look up. The token <em>mechanism</em> is borrowed
 * whole (a random secret, only its SHA-256 kept here); the storage is not.
 *
 * <p>The address is normalised to {@code lower(trim())} before it ever gets here, so comparisons and
 * the "one live invitation per (team, address)" index need no {@code lower()} of their own.
 *
 * <p>{@code domainId} is denormalised even though {@code team} implies it. It answers "which
 * invitations are waiting for this address?" — the question asked right after someone signs up —
 * without joining teams, and it mirrors {@code AuthToken.domainId}.
 */
@Setter
@Getter
@Entity
@Table(
    name = "team_invitations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"token_hash"})},
    indexes = {
      @Index(name = "idx_team_invitations_email", columnList = "domain_id, email, status"),
      @Index(name = "idx_team_invitations_team", columnList = "team_id, created_at"),
      @Index(name = "idx_team_invitations_creator", columnList = "created_by_id, created_at")
    })
@NoArgsConstructor
public class TeamInvitation extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Column(name = "domain_id", nullable = false)
  private Long domainId;

  @Column(name = "email", nullable = false, length = 250)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private TeamRole role;

  @Column(name = "token_hash", nullable = false, length = 100)
  private String tokenHash;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private InvitationStatus status = InvitationStatus.PENDING;

  @Column(name = "expires_at", nullable = false)
  private Instant expiresAt;

  @Column(name = "accepted_at")
  @Nullable
  private Instant acceptedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "accepted_by_id")
  @Nullable
  private User acceptedBy;

  @Column(name = "revoked_at")
  @Nullable
  private Instant revokedAt;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "revoked_by_id")
  @Nullable
  private User revokedBy;

  public TeamInvitation(
      User inviter, Team team, String email, TeamRole role, String tokenHash, Instant expiresAt) {
    super(inviter);
    this.team = team;
    this.domainId = team.getDomain().getId();
    this.email = email;
    this.role = role;
    this.tokenHash = tokenHash;
    this.expiresAt = expiresAt;
    this.status = InvitationStatus.PENDING;
  }

  public boolean isPending() {
    return status == InvitationStatus.PENDING;
  }

  /** Pending, but past its date — not yet marked, because nothing has looked at it since. */
  public boolean isLapsed() {
    return isPending() && expiresAt.isBefore(Instant.now());
  }

  public void markAccepted(User acceptedBy) {
    this.status = InvitationStatus.ACCEPTED;
    this.acceptedAt = Instant.now();
    this.acceptedBy = acceptedBy;
  }

  public void markRevoked(@Nullable User revokedBy) {
    this.status = InvitationStatus.REVOKED;
    this.revokedAt = Instant.now();
    this.revokedBy = revokedBy;
  }

  public void markExpired() {
    this.status = InvitationStatus.EXPIRED;
  }
}
