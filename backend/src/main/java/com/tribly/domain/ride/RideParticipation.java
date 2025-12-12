package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "ride_participations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"ride_group_id", "user_id"})
})
public class RideParticipation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_group_id", nullable = false)
    @NotNull
    private RideGroup rideGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ParticipationStatus status = ParticipationStatus.REGISTERED;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public RideParticipation() {
    }

    public RideParticipation(RideGroup rideGroup, User user) {
        this.rideGroup = rideGroup;
        this.user = user;
        this.registeredAt = Instant.now();
    }

    @PrePersist
    protected void onPrePersist() {
        if (registeredAt == null) {
            registeredAt = Instant.now();
        }
    }

    public RideGroup getRideGroup() {
        return rideGroup;
    }

    public void setRideGroup(RideGroup rideGroup) {
        this.rideGroup = rideGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public void setStatus(ParticipationStatus status) {
        this.status = status;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
