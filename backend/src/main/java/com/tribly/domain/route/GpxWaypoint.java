package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;

@Setter
@Getter
@Entity
@Table(
    name = "gpx_waypoints",
    indexes = {@Index(name = "idx_gpx_waypoints_route", columnList = "route_id")})
@NoArgsConstructor
public class GpxWaypoint extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @Column(name = "name", nullable = false, length = 250)
  protected String name;

  @Column(name = "geometry", columnDefinition = "geometry(Point,4326)", nullable = false)
  private Point<G2D> geometry;

  public GpxWaypoint(User createdBy, String name, Point<G2D> geometry) {
    super(createdBy);
    this.name = name;
    this.geometry = geometry;
  }
}
