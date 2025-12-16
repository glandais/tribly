package com.tribly.domain.route;

import com.tribly.domain.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Represents a climb segment detected within a route.
 * Contains metrics like elevation gain, gradients, and climb categorization.
 */
@Entity
@Table(name = "route_climbs")
public class RouteClimb extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @Column(name = "name")
    private String name;

    /**
     * Distance from route start where climb begins (in meters).
     */
    @NotNull
    @Column(name = "start_distance", nullable = false)
    private Integer startDistance;

    /**
     * Distance from route start where climb ends (in meters).
     */
    @NotNull
    @Column(name = "end_distance", nullable = false)
    private Integer endDistance;

    /**
     * Total elevation gain for this climb (in meters).
     */
    @NotNull
    @Column(name = "elevation_gain", nullable = false)
    private Integer elevationGain;

    /**
     * Average gradient percentage for the climb.
     */
    @NotNull
    @Column(name = "average_gradient", precision = 5, scale = 2, nullable = false)
    private BigDecimal averageGradient;

    /**
     * Maximum gradient percentage encountered in the climb.
     */
    @NotNull
    @Column(name = "max_gradient", precision = 5, scale = 2, nullable = false)
    private BigDecimal maxGradient;

    /**
     * Climb category based on difficulty (HC, CAT1, CAT2, CAT3, CAT4).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "category", length = 10)
    private ClimbCategory category;

    // Constructors

    public RouteClimb() {
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

    public Integer getStartDistance() {
        return startDistance;
    }

    public void setStartDistance(Integer startDistance) {
        this.startDistance = startDistance;
    }

    public Integer getEndDistance() {
        return endDistance;
    }

    public void setEndDistance(Integer endDistance) {
        this.endDistance = endDistance;
    }

    public Integer getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(Integer elevationGain) {
        this.elevationGain = elevationGain;
    }

    public BigDecimal getAverageGradient() {
        return averageGradient;
    }

    public void setAverageGradient(BigDecimal averageGradient) {
        this.averageGradient = averageGradient;
    }

    public BigDecimal getMaxGradient() {
        return maxGradient;
    }

    public void setMaxGradient(BigDecimal maxGradient) {
        this.maxGradient = maxGradient;
    }

    public ClimbCategory getCategory() {
        return category;
    }

    public void setCategory(ClimbCategory category) {
        this.category = category;
    }
}
