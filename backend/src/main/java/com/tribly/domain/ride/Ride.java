package com.tribly.domain.ride;

import com.tribly.domain.common.BaseEntity;
import com.tribly.domain.common.Visibility;
import com.tribly.domain.place.Place;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@Table(
    name = "rides",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_rides_team_slug",
          columnNames = {"team_id", "slug"})
    })
public class Ride extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_id", nullable = false)
  private User createdBy;

  @NotBlank
  @Size(max = 255)
  @Column(name = "title", nullable = false)
  private String title;

  @NotBlank
  @Size(max = 100)
  @Pattern(
      regexp = "^[a-z0-9-]+$",
      message = "Slug must contain only lowercase letters, numbers, and hyphens")
  @Column(name = "slug", nullable = false)
  private String slug;

  @Column(name = "description", columnDefinition = "TEXT")
  @Nullable
  private String description;

  @Column(name = "date", nullable = false)
  private LocalDate date;

  @Column(name = "start_time")
  @Nullable
  private LocalTime startTime;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meeting_point_id")
  @Nullable
  private Place meetingPoint;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private RideStatus status = RideStatus.DRAFT;

  @Enumerated(EnumType.STRING)
  @Column(name = "visibility", nullable = false, length = 20)
  private Visibility visibility = Visibility.TEAM;

  @Nullable
  @Column(name = "recurrence_rule")
  private String recurrenceRule;

  @Nullable
  @Column(name = "publish_at")
  private Instant publishAt;

  @Nullable
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_ride_id")
  private Ride parentRide;

  @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RideGroup> groups = new ArrayList<>();

  public Ride() {}

  public Ride(Team team, User createdBy, String title, String slug, LocalDate date) {
    this.team = team;
    this.createdBy = createdBy;
    this.title = title;
    this.slug = slug;
    this.date = date;
  }

  public void addGroup(RideGroup group) {
    groups.add(group);
    group.setRide(this);
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
