package com.tribly.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RouteSortBy {
  DISTANCE("te.distance"),
  ELEVATION_GAIN("te.elevationGain"),
  HILLINESS("te.hilliness"),
  DATE_TIME("te.dateTime");

  final String field;
}
