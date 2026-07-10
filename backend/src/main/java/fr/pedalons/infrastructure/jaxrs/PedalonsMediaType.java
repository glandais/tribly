package fr.pedalons.infrastructure.jaxrs;

/**
 * Media types missing from {@link jakarta.ws.rs.core.MediaType}.
 */
public final class PedalonsMediaType {

  /** Mapbox Vector Tile (protobuf). */
  public static final String MAPBOX_VECTOR_TILE = "application/vnd.mapbox-vector-tile";

  private PedalonsMediaType() {}
}
