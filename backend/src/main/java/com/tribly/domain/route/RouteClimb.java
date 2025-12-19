package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Represents a climb segment detected within a route.
 * Contains metrics like elevation gain, gradients, and climb categorization.
 */
@Setter
@Getter
@Entity
@Table(name = "route_climbs")
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

    // Constructors

    public RouteClimb() {
    }

    // Getters and Setters

}
