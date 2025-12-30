package com.tribly.domain.trip;

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
    name = "trip_participations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"trip_id", "user_id"})})
@NoArgsConstructor
public class TripParticipation extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id", nullable = false)
  private Trip trip;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "registered_at", nullable = false)
  private Instant registeredAt = Instant.now();

  public TripParticipation(Trip trip, User user) {
    super(user);
    this.trip = trip;
    this.user = user;
  }
}
