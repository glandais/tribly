package com.tribly.domain.ride;

import com.tribly.domain.common.Publication;
import com.tribly.domain.place.Place;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("1")
@NoArgsConstructor
public class Ride extends Publication {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<RideGroup> groups = new ArrayList<>();

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "place_start_id")
  @Nullable
  private Place start;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "place_end_id")
  @Nullable
  private Place end;

  public Ride(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
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

  @Override
  public EntityType getEntityType() {
    return EntityType.RIDE;
  }
}
