package com.tribly.service.team.response;

import com.tribly.domain.team.Team;
import com.tribly.enums.TeamRole;
import org.jspecify.annotations.Nullable;

public record TeamAndRole(Team team, @Nullable TeamRole teamRole, Long memberCount) {
  public boolean hasCapacity() {
    return team.getMaxMembers() == null || memberCount < team.getMaxMembers();
  }
}
