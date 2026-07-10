package fr.pedalons.service.team.request;

import fr.pedalons.enums.TeamRole;
import java.util.List;

public enum MinRole {
  MEMBER,
  ORGANIZER,
  ADMIN;

  /**
   * The team roles that satisfy this minimum. {@link TeamRole} carries no ordering, so the
   * hierarchy is spelled out rather than compared.
   */
  public List<TeamRole> acceptedRoles() {
    return switch (this) {
      case MEMBER -> List.of(TeamRole.MEMBER, TeamRole.ORGANIZER, TeamRole.ADMIN);
      case ORGANIZER -> List.of(TeamRole.ORGANIZER, TeamRole.ADMIN);
      case ADMIN -> List.of(TeamRole.ADMIN);
    };
  }
}
