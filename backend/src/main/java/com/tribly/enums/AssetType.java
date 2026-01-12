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
  ROUTE_THUMBNAIL(true);

  final boolean system;
}
