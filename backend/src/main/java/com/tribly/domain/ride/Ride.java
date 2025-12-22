package com.tribly.domain.ride;

import com.tribly.domain.common.TeamPublicationEntity;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import jakarta.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("1")
public class Ride extends TeamPublicationEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RideGroup> groups = new ArrayList<>();

  public Ride() {
    super();
  }

  public Ride(Team team, User createdBy, String name, String slug, LocalDateTime dateTime) {
    super(team, createdBy, name, slug, dateTime);
  }

  public void addGroup(RideGroup group) {
    groups.add(group);
    group.setRide(this);
  }

  public int getParticipantCount() {
    // FIXME
    return groups.stream()
        .filter(g -> !g.isDeleted())
        .mapToInt(RideGroup::getCurrentParticipants)
        .sum();
  }

  public int getGroupCount() {
    return (int) groups.stream().filter(g -> !g.isDeleted()).count();
  }
}
