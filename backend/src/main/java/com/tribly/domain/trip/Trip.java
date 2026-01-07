package com.tribly.domain.trip;

import com.tribly.domain.common.Publication;
import com.tribly.domain.route.Route;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("5")
@NoArgsConstructor
public class Trip extends Publication {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "route_id")
  @Nullable
  private Route route;

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TripStage> stages = new ArrayList<>();

  @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<TripParticipation> participations = new ArrayList<>();

  public Trip(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
  }

  public void addStage(TripStage stage) {
    stages.add(stage);
    stage.setTrip(this);
  }

  public void addParticipation(TripParticipation participation) {
    participations.add(participation);
    participation.setTrip(this);
  }

  public int getParticipantCount() {
    return (int) participations.stream().filter(p -> !p.isDeleted()).count();
  }

  public int getStageCount() {
    return (int) stages.stream().filter(s -> !s.isDeleted()).count();
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.TRIP;
  }
}
