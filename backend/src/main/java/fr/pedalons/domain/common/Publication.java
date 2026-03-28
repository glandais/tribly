package fr.pedalons.domain.common;

import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
public abstract class Publication extends TeamEntity {

  public Publication(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
  }
}
