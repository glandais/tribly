package com.tribly.service.common;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.repository.TeamEntityQueryInterface;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.UserTeam;
import com.tribly.domain.team.repository.UserTeamRepository;
import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;

public abstract class TeamEntityService<
    T extends TeamEntity, Q extends Query, RQ extends TeamEntityQueryInterface> {

  @Inject protected UserTeamRepository userTeamRepository;

  @Inject protected TeamSecurityService securityService;

  protected abstract TeamEntityRepository<T, RQ> getRepository();

  protected abstract RQ getQuery(Q query, Set<Long> memberTeamIds, Set<Long> organizerTeamIds);

  public TriblyPage<T> list(Q query) {

    // Step 1: Fetch user's team memberships
    Set<Long> memberTeamIds = new HashSet<>();
    Set<Long> organizerTeamIds = new HashSet<>();

    Long userId = query.userId();
    if (userId != null) {
      Set<String> teamSlugs = query.teamSlugs();
      List<UserTeam> userTeams = userTeamRepository.findByUserId(userId);
      memberTeamIds =
          userTeams.stream()
              .filter(ut -> isTeamSlugMatches(teamSlugs, ut))
              .map(ut -> ut.getTeam().getId())
              .collect(Collectors.toSet());
      organizerTeamIds =
          userTeams.stream()
              .filter(ut -> isTeamSlugMatches(teamSlugs, ut))
              .filter(UserTeam::isOrganizer)
              .map(ut -> ut.getTeam().getId())
              .collect(Collectors.toSet());
    }

    RQ repositoryQuery = getQuery(query, memberTeamIds, organizerTeamIds);

    return getRepository().find(repositoryQuery);
  }

  private boolean isTeamSlugMatches(@Nullable Set<String> teamSlugs, UserTeam ut) {
    if (teamSlugs != null) {
      return teamSlugs.contains(ut.getTeam().getSlug());
    } else {
      return true;
    }
  }
}
