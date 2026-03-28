package fr.pedalons.domain.team;

import fr.pedalons.domain.common.BaseEntity;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.TeamRole;
import jakarta.persistence.*;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(
    name = "user_teams",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "team_id"})},
    indexes = {@Index(name = "idx_user_teams_user_team", columnList = "user_id, team_id")})
@NoArgsConstructor
public class UserTeam extends BaseEntity {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "team_id", nullable = false)
  private Team team;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  private TeamRole role;

  @Column(name = "joined_at", nullable = false)
  private Instant joinedAt;

  public UserTeam(User creator, User user, Team team, TeamRole role) {
    super(creator);
    this.user = user;
    this.team = team;
    this.role = role;
    this.joinedAt = Instant.now();
  }

  public boolean isAdmin() {
    return role.isAdmin();
  }

  public boolean isOrganizer() {
    return role.isOrganizer();
  }
}
