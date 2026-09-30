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

  /**
   * Signed by the team: readers are not told who wrote it, only the team's administrators and the
   * author themself are. Defaults to the team's {@link
   * fr.pedalons.domain.team.Team#isPostsAsTeamByDefault()} when a post is created. docs/LEDGER_*.md
   * API-6.
   */
  @Column(name = "signed_as_team")
  private boolean signedAsTeam = true;

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
