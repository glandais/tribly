package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

/**
 * Represents a GPX track associated with a route.
 * Stores both PostGIS geometry for spatial queries and JSONB track points for efficient frontend rendering.
 */
@Setter
@Getter
@Entity
@Table(name = "gpx_tracks")
@NoArgsConstructor
public class GpxTrack extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  /**
   * PostGIS LineString geometry in WKT format.
   * Example: "LINESTRING(6.0 45.0, 6.1 45.1, ...)"
   * Used for spatial queries and route analysis.
   */
  @Column(name = "geometry", columnDefinition = "geometry(LineString,4326)", nullable = false)
  @org.hibernate.annotations.ColumnTransformer(
      write = "ST_GeomFromText(?, 4326)",
      read = "ST_AsText(geometry)")
  private String geometry;

  /**
   * Simplified track points stored as JSONB for efficient frontend consumption.
   * Contains lat, lng, elevation, and cumulative distance for each point.
   */
  @Type(JsonBinaryType.class)
  @Column(name = "track_points", columnDefinition = "jsonb", nullable = false)
  private List<TrackPoint> trackPoints;

  @Column(name = "processed_at", nullable = false)
  private Instant processedAt;

  public GpxTrack(
      User createdBy,
      Route route,
      String geometry,
      List<TrackPoint> trackPoints,
      Instant processedAt) {
    super(createdBy);
    this.route = route;
    this.geometry = geometry;
    this.trackPoints = trackPoints;
    this.processedAt = processedAt;
  }

  /**
   * Simplified track point for frontend rendering.
   * Stored as JSONB for efficient queries and minimal data transfer.
   */
  public record TrackPoint(
      double lat,
      double lng,
      double ele, // Elevation in meters
      double dist // Cumulative distance from start in meters
      ) {}
}
