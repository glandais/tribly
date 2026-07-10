package fr.pedalons.api.routes;

import fr.pedalons.infrastructure.jaxrs.PedalonsMediaType;
import jakarta.ws.rs.core.CacheControl;
import jakarta.ws.rs.core.Response;

/**
 * Shared response shape for the route vector tile endpoints.
 */
final class RouteTiles {

  /** Tiles depend on who is asking, so they may only be cached by the browser. */
  private static final int MAX_AGE_SECONDS = 300;

  private RouteTiles() {}

  static Response response(byte[] tile) {
    CacheControl cacheControl = new CacheControl();
    cacheControl.setPrivate(true);
    cacheControl.setMaxAge(MAX_AGE_SECONDS);
    return Response.ok(tile, PedalonsMediaType.MAPBOX_VECTOR_TILE)
        .cacheControl(cacheControl)
        .build();
  }
}
