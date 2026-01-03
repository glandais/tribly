package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.NotNullableDbValue;
import com.tribly.domain.user.User;
import io.github.glandais.gpx.climb.Climb;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.LineString;
import org.hibernate.annotations.Type;

/**
 * Represents a GPX track associated with a route.
 * Stores both PostGIS geometry for spatial queries and JSONB track points for efficient frontend rendering.
 */
@Setter
@Getter
@Entity
@Table(
    name = "gpx_tracks",
    indexes = {@Index(name = "idx_gpx_tracks_route_deleted", columnList = "route_id, deleted")})
@NoArgsConstructor
public class GpxTrack extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  protected String name;

  @Column(name = "geometry", columnDefinition = "geometry(LineString,4326)", nullable = false)
  private LineString<G2D> geometry;

  @Type(JsonBinaryType.class)
  @Column(name = "track_points", columnDefinition = "jsonb", nullable = false)
  private List<TrackPoint> trackPoints;

  @Type(JsonBinaryType.class)
  @Column(name = "climbs", columnDefinition = "jsonb", nullable = false)
  private List<Climb> climbs;

  @Column(name = "distance", nullable = false)
  @NotNullableDbValue
  private Integer distance;

  @Column(name = "elevation_gain", nullable = false)
  @NotNullableDbValue
  private Integer elevationGain;

  @Column(name = "elevation_loss", nullable = false)
  @NotNullableDbValue
  private Integer elevationLoss;

  public GpxTrack(
      User createdBy,
      String name,
      LineString<G2D> geometry,
      List<TrackPoint> trackPoints,
      List<Climb> climbs,
      int distance,
      int elevationGain,
      int elevationLoss) {
    super(createdBy);
    this.name = name;
    this.geometry = geometry;
    this.trackPoints = trackPoints;
    this.climbs = climbs;
    this.distance = distance;
    this.elevationGain = elevationGain;
    this.elevationLoss = elevationLoss;
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
