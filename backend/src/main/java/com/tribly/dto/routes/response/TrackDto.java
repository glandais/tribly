package com.tribly.dto.routes.response;

import com.tribly.domain.route.GpxTrack;
import com.tribly.dto.validation.ValidateSchema;
import java.util.List;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

/**
 * GPX Track DTO with track points for frontend rendering.
 */
@Schema(description = "GPX track with track points")
@ValidateSchema
public record TrackDto(
    @Schema(description = "List of track points", required = true) List<TrackPointDto> trackPoints,
    @Schema(description = "List of climbs on the route", required = true) List<ClimbDto> climbs) {
  public static TrackDto from(GpxTrack track) {
    List<TrackPointDto> points =
        track.getTrackPoints().stream()
            .map(p -> new TrackPointDto(p.lat(), p.lng(), p.ele(), p.dist()))
            .toList();
    List<ClimbDto> routeClimbDtos = track.getClimbs().stream().map(ClimbDto::from).toList();

    return new TrackDto(points, routeClimbDtos);
  }

  @Schema(description = "GPS track point")
  @ValidateSchema
  public record TrackPointDto(
      @Schema(description = "Latitude", required = true) double lat,
      @Schema(description = "Longitude", required = true) double lng,
      @Schema(description = "Elevation in meters", required = true) double ele,
      @Schema(description = "Distance from start in meters", required = true) double dist) {}
}
