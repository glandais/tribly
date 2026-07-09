package fr.pedalons.domain.trip;

import fr.pedalons.domain.common.NotNullableDbValue;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.place.Place;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Visibility;
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
  @NotNullableDbValue
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
  @NotNullableDbValue
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

  /** Convenience constructor that derives team, date and visibility from the trip. */
  public TripStage(User createdBy, Trip trip, String name, String slug) {
    super(createdBy, trip.getTeam(), trip.getDateTime(), name, slug, trip.getVisibility());
    this.trip = trip;
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.TRIP_STAGE;
  }
}
