package com.tribly.dto.places.response;

import com.tribly.domain.place.Place;
import com.tribly.dto.common.GeoJsonPoint;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.jspecify.annotations.Nullable;

public record PlaceDetailDto(
    @Schema(description = "Place ID (TSID)", examples = "0h4a8xzk8jv80", required = true) String id,
    @Schema(required = true) String name,
    @Nullable String address,
    @Nullable String link,
    @Schema(required = true) boolean startPlace,
    @Schema(required = true) boolean endPlace,
    @Schema(implementation = GeoJsonPoint.class) @Nullable Point<G2D> geometry) {
  public static PlaceDetailDto from(Place start) {
    return new PlaceDetailDto(
        TsidUtils.toString(start.getId()),
        start.getName(),
        start.getAddress(),
        start.getLink(),
        start.isStartPlace(),
        start.isEndPlace(),
        start.getGeometry());
  }
}
