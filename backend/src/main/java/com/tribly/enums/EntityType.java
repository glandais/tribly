package com.tribly.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;

@RequiredArgsConstructor
@Getter
public enum EntityType {
  TEAM(0, null),
  USER(1, null),
  USER_TEAM(2, null),
  RIDE_TEMPLATE(3, null),
  RIDE_TEMPLATE_GROUP(4, null),
  PLACE(5, null),
  COMMENT(6, null),
  ASSET(7, null),
  TRIP(8, TeamEntityType.TRIP),
  TRIP_STAGE(9, TeamEntityType.TRIP_STAGE),
  ROUTE(10, TeamEntityType.ROUTE),
  RIDE(11, TeamEntityType.RIDE),
  RIDE_GROUP(12, null),
  AD(13, TeamEntityType.AD),
  POST(14, TeamEntityType.POST),
  TEAM_PAGE(15, TeamEntityType.TEAM_PAGE),
  PUBLICATION(-1, null),
  ANY(-1, null);

  private final int stableOrdinal;
  private final @Nullable TeamEntityType teamEntityType;
}
