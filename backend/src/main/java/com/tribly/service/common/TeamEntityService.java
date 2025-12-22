package com.tribly.service.common;

import com.tribly.domain.team.Team;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

public class TeamEntityService {

  @Inject protected TeamSecurityService securityService;

  protected @Nullable Visibility getVisibility(@Nullable Long userId, Team team) {
    boolean isMember = securityService.isMember(userId, team);
    if (isMember) {
      return null;
    } else if (team.getVisibility() == Visibility.PUBLIC) {
      // For non-members of public teams, filter to show only public rides
      return Visibility.PUBLIC;
    } else {
      // Private team - no access for non-members
      throw BusinessException.notFound("Private team");
    }
  }
}
