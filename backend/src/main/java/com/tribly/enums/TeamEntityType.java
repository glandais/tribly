package com.tribly.enums;

import com.tribly.domain.ad.Ad;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.post.Post;
import com.tribly.domain.ride.Ride;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.trip.Trip;
import com.tribly.domain.trip.TripStage;
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
