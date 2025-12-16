package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;

/**
 * Represents a GPX track associated with a route.
 * Stores both PostGIS geometry for spatial queries and JSONB track points for efficient frontend rendering.
 */
@Entity
@Table(name = "gpx_tracks")
public class GpxTrack extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "name")
    private String name;

    /**
     * PostGIS LineString geometry in WKT format.
     * Example: "LINESTRING(6.0 45.0, 6.1 45.1, ...)"
     * Used for spatial queries and route analysis.
     */
    @Column(name = "geometry", columnDefinition = "geometry(LineString,4326)", nullable = false)
    @org.hibernate.annotations.ColumnTransformer(
            write = "ST_GeomFromText(?, 4326)",
            read = "ST_AsText(geometry)"
    )
    private String geometry;

    /**
     * Simplified track points stored as JSONB for efficient frontend consumption.
     * Contains lat, lng, elevation, and cumulative distance for each point.
     */
    @Type(JsonBinaryType.class)
    @Column(name = "track_points", columnDefinition = "jsonb", nullable = false)
    private List<TrackPoint> trackPoints;

    @Column(name = "original_file_name")
    private String originalFileName;

    @Column(name = "processed_at", nullable = false)
    private Instant processedAt;

    // Constructors

    public GpxTrack() {
    }

    // Getters and Setters

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public List<TrackPoint> getTrackPoints() {
        return trackPoints;
    }

    public void setTrackPoints(List<TrackPoint> trackPoints) {
        this.trackPoints = trackPoints;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    /**
     * Simplified track point for frontend rendering.
     * Stored as JSONB for efficient queries and minimal data transfer.
     */
    public record TrackPoint(
            double lat,
            double lng,
            double ele,  // Elevation in meters
            double dist // Cumulative distance from start in meters
    ) {
    }
}
