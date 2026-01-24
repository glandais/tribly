package com.tribly.service.admin;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.NotFoundException;
import com.tribly.domain.team.Team;
import com.tribly.dto.admin.AdminTeamDto;
import com.tribly.dto.common.TriblyPage;
import com.tribly.dto.error.ErrorCode;
import com.tribly.repository.team.TeamRepository;
import com.tribly.repository.team.UserTeamRepository;
import com.tribly.service.security.annotation.Admin;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class AdminTeamService {

  @Inject TeamRepository teamRepository;

  @Inject UserTeamRepository userTeamRepository;

  @Admin
  public TriblyPage<AdminTeamDto> listTeams(@Nullable String domainId, int page, int size) {
    String query = "deleted = false";
    Object[] params = new Object[0];

    if (domainId != null) {
      Long domainIdLong = TsidUtils.toLong(domainId);
      query = "domain.id = ?1 and deleted = false";
      params = new Object[] {domainIdLong};
    }

    List<Team> teams =
        teamRepository.find(query + " order by createdAt desc", params).page(page, size).list();
    long total = teamRepository.count(query, params);

    List<AdminTeamDto> items =
        teams.stream()
            .map(
                team -> {
                  long memberCount =
                      userTeamRepository.count("team.id = ?1 and deleted = false", team.getId());
                  return AdminTeamDto.from(team, memberCount);
                })
            .toList();

    return new TriblyPage<>(items, total);
  }

  @Admin
  public AdminTeamDto getTeam(String teamId) {
    Team team = findTeam(teamId);
    long memberCount = userTeamRepository.count("team.id = ?1 and deleted = false", team.getId());
    return AdminTeamDto.from(team, memberCount);
  }

  @Admin
  @Transactional
  public AdminTeamDto toggleTeamDeleted(String teamId) {
    Team team = findTeamIncludingDeleted(teamId);
    // Toggle soft-delete
    team.setDeleted(!team.isDeleted());
    teamRepository.persist(team);
    long memberCount = userTeamRepository.count("team.id = ?1 and deleted = false", team.getId());
    return AdminTeamDto.from(team, memberCount);
  }

  private Team findTeam(String teamId) {
    Long id = TsidUtils.toLong(teamId);
    return teamRepository
        .find("id = ?1 and deleted = false", id)
        .firstResultOptional()
        .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
  }

  private Team findTeamIncludingDeleted(String teamId) {
    Long id = TsidUtils.toLong(teamId);
    return teamRepository
        .find("id = ?1", id)
        .firstResultOptional()
        .orElseThrow(() -> new NotFoundException(ErrorCode.TEAM_NOT_FOUND));
  }
}
