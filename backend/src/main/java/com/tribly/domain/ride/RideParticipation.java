package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import com.tribly.enums.ParticipationStatus;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "ride_participations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"ride_group_id", "user_id"})})
public class RideParticipation extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ride_group_id", nullable = false)
  private RideGroup rideGroup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private ParticipationStatus status = ParticipationStatus.REGISTERED;

  @Column(name = "registered_at", nullable = false)
  private Instant registeredAt;

  @Column(name = "notes", columnDefinition = "TEXT")
  @Nullable
  private String notes;

  public RideParticipation() {
    this.registeredAt = Instant.now();
  }

  public RideParticipation(RideGroup rideGroup, User user) {
    this.rideGroup = rideGroup;
    this.user = user;
    this.registeredAt = Instant.now();
  }
}
