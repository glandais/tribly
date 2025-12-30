package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "ride_participations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"ride_group_id", "user_id"})})
@NoArgsConstructor
public class RideParticipation extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ride_group_id", nullable = false)
  private RideGroup rideGroup;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "registered_at", nullable = false)
  private Instant registeredAt = Instant.now();

  public RideParticipation(RideGroup rideGroup, User user) {
    super(user);
    this.rideGroup = rideGroup;
    this.user = user;
  }
}
