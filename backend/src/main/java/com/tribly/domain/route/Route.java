package com.tribly.domain.route;

import com.tribly.domain.common.NotNullableDbValue;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.EntityType;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import com.tribly.enums.WindDirection;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

@Setter
@Getter
@Entity
@DiscriminatorValue("2")
@NoArgsConstructor
public class Route extends TeamEntity {

  @Column(name = "distance")
  @NotNullableDbValue
  private Integer distance;

  @Column(name = "elevation_gain")
  @NotNullableDbValue
  private Integer elevationGain;

  @Column(name = "hilliness")
  @NotNullableDbValue
  private Integer hilliness;

  @Column(name = "elevation_loss")
  @NotNullableDbValue
  private Integer elevationLoss;

  @Enumerated(EnumType.STRING)
  @Column(name = "surface_type", length = 20)
  @NotNullableDbValue
  private SurfaceType surfaceType;

  @Column(name = "`start`", columnDefinition = "geometry(Point,4326)")
  @NotNullableDbValue
  private Point<G2D> start;

  @Column(name = "`end`", columnDefinition = "geometry(Point,4326)")
  @NotNullableDbValue
  private Point<G2D> end;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "wind_direction", length = 20)
  @NotNullableDbValue
  private WindDirection windDirection;

  @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GpxTrack> tracks = new ArrayList<>();

  @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<GpxWaypoint> waypoints = new ArrayList<>();

  public Route(
      User createdBy,
      Team team,
      String name,
      String slug,
      Visibility visibility,
      SurfaceType surfaceType) {
    super(createdBy, team, Instant.now(), name, slug, visibility);
    this.surfaceType = surfaceType;
  }

  public void addTrack(GpxTrack track) {
    tracks.add(track);
    track.setRoute(this);
  }

  public void addWaypoint(GpxWaypoint waypoint) {
    waypoints.add(waypoint);
    waypoint.setRoute(this);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.ROUTE;
  }
}
