package com.tribly.domain.route;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.SurfaceType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("2")
public class Route extends TeamEntity {

  @Column(name = "distance")
  private Integer distance;

  @Column(name = "elevation_gain")
  private Integer elevationGain;

  @Column(name = "elevation_loss")
  private Integer elevationLoss;

  @Enumerated(EnumType.STRING)
  @Column(name = "surface_type", length = 20)
  private SurfaceType surfaceType;

  @Column(name = "start_lat", precision = 10, scale = 8)
  private BigDecimal startLat;

  @Column(name = "start_lng", precision = 11, scale = 8)
  private BigDecimal startLng;

  @Column(name = "end_lat", precision = 10, scale = 8)
  private BigDecimal endLat;

  @Column(name = "end_lng", precision = 11, scale = 8)
  private BigDecimal endLng;

  public Route() {}

  public Route(Team team, User createdBy, String name, String slug) {
    this.team = team;
    this.createdBy = createdBy;
    this.name = name;
    this.slug = slug;
    this.dateTime = LocalDateTime.now();
  }
}
