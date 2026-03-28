package fr.pedalons.domain.post;

import fr.pedalons.domain.common.Publication;
import fr.pedalons.domain.team.Team;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@DiscriminatorValue("3")
@NoArgsConstructor
public class Post extends Publication {

  public Post(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.POST;
  }
}
