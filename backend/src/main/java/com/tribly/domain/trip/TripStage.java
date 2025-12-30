package com.tribly.domain.trip;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.place.Place;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("6")
@NoArgsConstructor
public class TripStage extends TeamEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "trip_id")
  private Trip trip;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "place_start_id")
  @Nullable
  private Place startPlace;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "place_end_id")
  @Nullable
  private Place endPlace;

  @Column(name = "sort_order")
  private int sortOrder = 0;

  public TripStage(
      User createdBy,
      Team team,
      Trip trip,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
    this.trip = trip;
  }

  /**
   * Convenience constructor that derives team and visibility from the trip.
   * Generates a temporary slug that should be updated by the service.
   */
  public TripStage(User createdBy, Trip trip, String name) {
    super(
        createdBy,
        trip.getTeam(),
        trip.getDateTime(),
        name,
        "stage-" + System.currentTimeMillis(),
        trip.getVisibility());
    this.trip = trip;
  }
}
