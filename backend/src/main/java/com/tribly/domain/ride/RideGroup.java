package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.route.Route;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "ride_groups",
    indexes = {
      @Index(columnList = "ride_id, deleted"),
    })
@NoArgsConstructor
public class RideGroup extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ride_id", nullable = false)
  private Ride ride;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @Nullable
  @Column(name = "time")
  protected LocalTime time;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @Column(name = "average_speed")
  @Nullable
  private Integer averageSpeed;

  @Column(name = "max_participants")
  @Nullable
  private Integer maxParticipants;

  @Column(name = "sort_order", nullable = false)
  private int sortOrder = 0;

  @OneToMany(mappedBy = "rideGroup", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RideParticipation> participations = new ArrayList<>();

  public RideGroup(User creator, Ride ride, String name) {
    super(creator);
    this.ride = ride;
    this.name = name;
  }

  public void addParticipation(RideParticipation participation) {
    participations.add(participation);
    participation.setRideGroup(this);
  }

  public int getCurrentParticipants() {
    // FIXME
    return (int) participations.stream().filter(p -> !p.isDeleted()).count();
  }

  public boolean hasCapacity() {
    return maxParticipants == null || getCurrentParticipants() < maxParticipants;
  }
}
