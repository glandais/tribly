package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.route.Route;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ride_groups")
public class RideGroup extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    @NotNull
    private Ride ride;

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "average_speed")
    private Integer averageSpeed;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @OneToMany(mappedBy = "rideGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideParticipation> participations = new ArrayList<>();

    public RideGroup() {
    }

    public RideGroup(Ride ride, String name) {
        this.ride = ride;
        this.name = name;
    }

    public Ride getRide() {
        return ride;
    }

    public void setRide(Ride ride) {
        this.ride = ride;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Integer getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Integer averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public List<RideParticipation> getParticipations() {
        return participations;
    }

    public void setParticipations(List<RideParticipation> participations) {
        this.participations = participations;
    }

    public void addParticipation(RideParticipation participation) {
        participations.add(participation);
        participation.setRideGroup(this);
    }

    public void removeParticipation(RideParticipation participation) {
        participations.remove(participation);
        participation.setRideGroup(null);
    }

    public int getCurrentParticipants() {
        return (int) participations.stream()
                .filter(p -> !p.isDeleted() && p.getStatus() != ParticipationStatus.CANCELLED)
                .count();
    }

    public boolean hasCapacity() {
        return maxParticipants == null || getCurrentParticipants() < maxParticipants;
    }
}
