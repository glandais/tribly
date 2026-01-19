package com.tribly.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GpsServiceType {
  HAMMERHEAD("Hammerhead"),
  GARMIN("Garmin Connect");

  private final String displayName;
}
