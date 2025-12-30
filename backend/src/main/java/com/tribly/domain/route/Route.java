package com.tribly.domain.route;

import com.tribly.domain.common.NotNullableDbValue;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

  @Column(name = "elevation_loss")
  @NotNullableDbValue
  private Integer elevationLoss;

  @Enumerated(EnumType.STRING)
  @Column(name = "surface_type", length = 20)
  @NotNullableDbValue
  private SurfaceType surfaceType;

  @Column(name = "start_lat", precision = 10, scale = 8)
  @NotNullableDbValue
  private BigDecimal startLat;

  @Column(name = "start_lng", precision = 11, scale = 8)
  @NotNullableDbValue
  private BigDecimal startLng;

  @Column(name = "end_lat", precision = 10, scale = 8)
  @NotNullableDbValue
  private BigDecimal endLat;

  @Column(name = "end_lng", precision = 11, scale = 8)
  @NotNullableDbValue
  private BigDecimal endLng;

  public Route(User createdBy, Team team, String name, String slug, Visibility visibility) {
    super(createdBy, team, Instant.now(), name, slug, visibility);
  }
}
