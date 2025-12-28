package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import com.tribly.enums.ClimbCategory;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

/**
 * Represents a climb segment detected within a route.
 * Contains metrics like elevation gain, gradients, and climb categorization.
 */
@Setter
@Getter
@Entity
@Table(name = "route_climbs")
@NoArgsConstructor
public class RouteClimb extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id", nullable = false)
  private Route route;

  @Column(name = "name")
  @Nullable
  private String name;

  /**
   * Distance from route start where climb begins (in meters).
   */
  @Column(name = "start_distance", nullable = false)
  private Integer startDistance;

  /**
   * Distance from route start where climb ends (in meters).
   */
  @Column(name = "end_distance", nullable = false)
  private Integer endDistance;

  /**
   * Total elevation gain for this climb (in meters).
   */
  @Column(name = "elevation_gain", nullable = false)
  private Integer elevationGain;

  /**
   * Average gradient percentage for the climb.
   */
  @Column(name = "average_gradient", precision = 5, scale = 2, nullable = false)
  private BigDecimal averageGradient;

  /**
   * Maximum gradient percentage encountered in the climb.
   */
  @Column(name = "max_gradient", precision = 5, scale = 2, nullable = false)
  private BigDecimal maxGradient;

  /**
   * Climb category based on difficulty (HC, CAT1, CAT2, CAT3, CAT4).
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "category", length = 10)
  @Nullable
  private ClimbCategory category;

  public RouteClimb(
      User createdBy,
      Route route,
      Integer startDistance,
      Integer endDistance,
      Integer elevationGain,
      BigDecimal averageGradient,
      BigDecimal maxGradient) {
    super(createdBy);
    this.route = route;
    this.startDistance = startDistance;
    this.endDistance = endDistance;
    this.elevationGain = elevationGain;
    this.averageGradient = averageGradient;
    this.maxGradient = maxGradient;
  }
}
