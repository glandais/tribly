package com.tribly.domain.team;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.user.User;
import com.tribly.enums.Visibility;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("4")
@NoArgsConstructor
public class TeamDescription extends TeamEntity {
  public TeamDescription(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
  }
}
