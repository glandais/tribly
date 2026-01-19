package com.tribly.infrastructure.gps;

import org.jspecify.annotations.Nullable;

/**
 * Result of uploading a route to a GPS service.
 */
public record RouteUploadResult(
    boolean success, @Nullable String message, @Nullable String externalRouteId) {
  public static RouteUploadResult success(@Nullable String externalRouteId) {
    return new RouteUploadResult(true, null, externalRouteId);
  }

  public static RouteUploadResult failure(String message) {
    return new RouteUploadResult(false, message, null);
  }
}
