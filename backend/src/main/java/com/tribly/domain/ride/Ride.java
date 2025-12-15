package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.place.Place;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rides", uniqueConstraints = {
    @UniqueConstraint(name = "uk_rides_team_slug", columnNames = {"team_id", "slug"})
})
public class Ride extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    @NotNull
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    @NotNull
    private User createdBy;

    @NotBlank
    @Size(max = 255)
    @Column(name = "title", nullable = false)
    private String title;

    @NotBlank
    @Size(max = 100)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "Slug must contain only lowercase letters, numbers, and hyphens")
    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meeting_point_id")
    private Place meetingPoint;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RideStatus status = RideStatus.DRAFT;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false, length = 20)
    private Visibility visibility = Visibility.TEAM;

    @Column(name = "recurrence_rule")
    private String recurrenceRule;

    @Column(name = "publish_at")
    private Instant publishAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_ride_id")
    private Ride parentRide;

    @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RideGroup> groups = new ArrayList<>();

    public Ride() {
    }

    public Ride(Team team, User createdBy, String title, String slug, LocalDate date) {
        this.team = team;
        this.createdBy = createdBy;
        this.title = title;
        this.slug = slug;
        this.date = date;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Place getMeetingPoint() {
        return meetingPoint;
    }

    public void setMeetingPoint(Place meetingPoint) {
        this.meetingPoint = meetingPoint;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public Instant getPublishAt() {
        return publishAt;
    }

    public void setPublishAt(Instant publishAt) {
        this.publishAt = publishAt;
    }

    public Ride getParentRide() {
        return parentRide;
    }

    public void setParentRide(Ride parentRide) {
        this.parentRide = parentRide;
    }

    public List<RideGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<RideGroup> groups) {
        this.groups = groups;
    }

    public void addGroup(RideGroup group) {
        groups.add(group);
        group.setRide(this);
    }

    public void removeGroup(RideGroup group) {
        groups.remove(group);
        group.setRide(null);
    }

    public int getParticipantCount() {
        return groups.stream()
                .filter(g -> !g.isDeleted())
                .mapToInt(RideGroup::getCurrentParticipants)
                .sum();
    }

    public int getGroupCount() {
        return (int) groups.stream().filter(g -> !g.isDeleted()).count();
    }
}
