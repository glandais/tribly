package com.tribly.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AssetType {
  LOGO(false),
  IMAGE(false),
  ATTACHMENT(false),
  ROUTE_ORIGINAL_GPX(true),
  ROUTE_FILTERED_GPX(true),
  ROUTE_FIT(true),
  ROUTE_THUMBNAIL_LIGHT(true),
  ROUTE_THUMBNAIL_DARK(true),
  RIDE_THUMBNAIL_LIGHT(true),
  RIDE_THUMBNAIL_DARK(true),
  TRIP_THUMBNAIL_LIGHT(true),
  TRIP_THUMBNAIL_DARK(true);

  final boolean system;
}
