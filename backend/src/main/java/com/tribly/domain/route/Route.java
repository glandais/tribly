package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.RouteDifficulty;
import com.tribly.enums.SurfaceType;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(name = "routes")
public class Route extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id", nullable = false)
  private User createdBy;

  @NotBlank
  @Size(max = 255)
  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  @Nullable
  private String description;

  @Column(name = "distance", nullable = false)
  private Integer distance;

  @Column(name = "elevation_gain", nullable = false)
  private Integer elevationGain;

  @Column(name = "elevation_loss", nullable = false)
  private Integer elevationLoss;

  @Enumerated(EnumType.STRING)
  @Column(name = "difficulty", length = 20, nullable = false)
  private RouteDifficulty difficulty;

  @Enumerated(EnumType.STRING)
  @Column(name = "surface_type", length = 20, nullable = false)
  private SurfaceType surfaceType;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  @Column(name = "thumbnail_url", length = 500, nullable = false)
  private String thumbnailUrl;

  @Column(name = "start_lat", precision = 10, scale = 8, nullable = false)
  private BigDecimal startLat;

  @Column(name = "start_lng", precision = 11, scale = 8, nullable = false)
  private BigDecimal startLng;

  @Column(name = "end_lat", precision = 10, scale = 8, nullable = false)
  private BigDecimal endLat;

  @Column(name = "end_lng", precision = 11, scale = 8, nullable = false)
  private BigDecimal endLng;

  public Route() {}

  public Route(Team team, User createdBy, String name) {
    this.team = team;
    this.createdBy = createdBy;
    this.name = name;
  }
}
