package fr.pedalons.domain.team;

import fr.pedalons.domain.common.NotNullableDbValue;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.EntityType;
import fr.pedalons.enums.Visibility;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;

@Setter
@Getter
@Entity
@DiscriminatorValue("4")
@NoArgsConstructor
public class TeamPage extends TeamEntity {

  @Nullable
  @Column(name = "page_order")
  private Integer pageOrder;

  @Column(name = "is_about_page")
  @NotNullableDbValue
  private boolean aboutPage = false;

  private TeamPage(
      User createdBy,
      Team team,
      Instant dateTime,
      String name,
      String slug,
      Visibility visibility) {
    super(createdBy, team, dateTime, name, slug, visibility);
  }

  public static TeamPage createAboutPage(User createdBy, Team team, Visibility visibility) {
    TeamPage page = new TeamPage(createdBy, team, Instant.now(), "about", "about", visibility);
    page.setAboutPage(true);
    return page;
  }

  public static TeamPage createAdditionalPage(
      User createdBy, Team team, String name, String slug, Visibility visibility, int order) {
    TeamPage page = new TeamPage(createdBy, team, Instant.now(), name, slug, visibility);
    page.setPageOrder(order);
    page.setAboutPage(false);
    return page;
  }

  @Override
  public EntityType getEntityType() {
    return EntityType.TEAM_PAGE;
  }
}
