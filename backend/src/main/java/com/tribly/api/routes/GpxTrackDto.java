package com.tribly.api.routes;

import com.tribly.domain.route.GpxTrack;
import com.tribly.infrastructure.id.TsidUtils;

import java.util.List;

/**
 * GPX Track DTO with track points for frontend rendering.
 */
public record GpxTrackDto(
        String id,
        String name,
        List<TrackPointDto> trackPoints,
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

    public record TrackPointDto(
            double lat,
            double lng,
            double ele,
            double dist
    ) {
    }
}
