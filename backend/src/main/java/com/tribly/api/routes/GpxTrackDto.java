package com.tribly.api.routes;

import com.tribly.domain.route.GpxTrack;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

/**
 * GPX Track DTO with track points for frontend rendering.
 */
@Schema(description = "GPX track with track points")
public record GpxTrackDto(
        @Schema(description = "Track ID (TSID)")
        String id,

        @Schema(description = "Track name")
        String name,

        @Schema(description = "List of track points")
        List<TrackPointDto> trackPoints,

        @Schema(description = "Processing timestamp")
        String processedAt
) {
    public static GpxTrackDto from(GpxTrack track) {
        List<TrackPointDto> points = track.getTrackPoints().stream()
                .map(p -> new TrackPointDto(p.lat(), p.lng(), p.ele(), p.dist()))
                .toList();

        return new GpxTrackDto(
                TsidUtils.toString(track.getId()),
                track.getName(),
                points,
                track.getProcessedAt() != null ? track.getProcessedAt().toString() : null
        );
    }

    @Schema(description = "GPS track point")
    public record TrackPointDto(
            @Schema(description = "Latitude")
            double lat,

            @Schema(description = "Longitude")
            double lng,

            @Schema(description = "Elevation in meters")
            double ele,

            @Schema(description = "Distance from start in meters")
            double dist
    ) {
    }
}
