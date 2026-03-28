package fr.pedalons.enums;

import fr.pedalons.domain.ad.Ad;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.post.Post;
import fr.pedalons.domain.ride.Ride;
import fr.pedalons.domain.route.Route;
import fr.pedalons.domain.team.TeamPage;
import fr.pedalons.domain.trip.Trip;
import fr.pedalons.domain.trip.TripStage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TeamEntityType {
  RIDE(1, "Ride"),
  ROUTE(2, "Route"),
  POST(3, "Post"),
  TEAM_PAGE(4, "TeamPage"),
  TRIP(5, "Trip"),
  TRIP_STAGE(6, "TripStage"),
  AD(7, "Ad"),
  PUBLICATION(-1, "Publication"),
  ANY(-1, "TeamEntity");

  private final int value;
  private final String typeName;

  public static TeamEntityType fromEntity(TeamEntity entity) {
    return switch (entity) {
      case Ride ignored -> RIDE;
      case Route ignored -> ROUTE;
      case Post ignored -> POST;
      case TeamPage ignored -> TEAM_PAGE;
      case Trip ignored -> TRIP;
      case TripStage ignored -> TRIP_STAGE;
      case Ad ignored -> AD;
      case null, default ->
          throw new IllegalArgumentException(
              "Unknown entity class: " + entity.getClass().getName());
    };
  }
}
