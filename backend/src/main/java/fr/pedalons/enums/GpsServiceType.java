package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GpsServiceType {
  HAMMERHEAD("Hammerhead"),
  GARMIN("Garmin Connect"),
  WAHOO("Wahoo");

  private final String displayName;
}
