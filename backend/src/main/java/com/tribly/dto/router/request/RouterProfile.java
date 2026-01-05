package com.tribly.dto.router.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RouterProfile {
  BIKE("Trekking-dry"),
  FASTBIKE("fastbike"),
  GRAVEL("gravel"),
  MTB("MTB"),
  RUN_HIKE("Hiking-Alpine-SAC6"),
  MOTORCYCLE("Car-FastEco");

  private final String profileName;
}
