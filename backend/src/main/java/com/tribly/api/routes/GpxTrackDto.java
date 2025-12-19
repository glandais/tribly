package com.tribly.api.routes;

import com.tribly.domain.route.GpxTrack;
import com.tribly.infrastructure.id.TsidUtils;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * GPX Track DTO with track points for frontend rendering.
 */
@Schema(description = "GPX track with track points")
public record GpxTrackDto(
        @Schema(description = "Track ID (TSID)", required = true)
        String id,

        @Nullable @Schema(description = "Track name")
        String name,

        @Schema(description = "List of track points", required = true)
        List<TrackPointDto> trackPoints,

        @Nullable @Schema(description = "Processing timestamp", nullable = true)
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
                track.getProcessedAt().toString()
        );
    }

    @Schema(description = "GPS track point")
    public record TrackPointDto(
            @Schema(description = "Latitude", required = true)
            double lat,

            @Schema(description = "Longitude", required = true)
            double lng,

            @Schema(description = "Elevation in meters", required = true)
            double ele,

            @Schema(description = "Distance from start in meters", required = true)
            double dist
    ) {
    }
}
