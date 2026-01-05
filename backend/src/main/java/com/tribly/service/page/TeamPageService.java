package com.tribly.service.page;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.team.repository.TeamPageRepository;
import com.tribly.domain.user.User;
import com.tribly.dto.pages.request.ReorderPagesRequest;
import com.tribly.dto.pages.request.TeamPageRequest;
import com.tribly.dto.pages.response.TeamPageDto;
import com.tribly.dto.pages.response.TeamPageSummaryDto;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamPageService extends TeamEntityService {

  private static final Logger LOG = Logger.getLogger(TeamPageService.class);
  private static final int MAX_ADDITIONAL_PAGES = 3;

  @Inject TeamPageRepository teamPageRepository;

  public List<TeamPageSummaryDto> listPages(String teamSlug, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    boolean isMember = userId != null && securityService.isMember(userId, team);

    return team.getAdditionalPages().stream()
        .filter(page -> canViewPage(page, isMember))
        .map(TeamPageSummaryDto::from)
        .toList();
  }

  public TeamPageDto getPage(String teamSlug, String pageSlug, @Nullable Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), pageSlug)
            .orElseThrow(() -> BusinessException.notFound("Page", pageSlug));

    // Check visibility
    boolean isMember = userId != null && securityService.isMember(userId, team);
    if (!canViewPage(page, isMember)) {
      throw BusinessException.forbidden("You don't have permission to view this page");
    }

    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public TeamPageDto createPage(String teamSlug, TeamPageRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    User creator =
        userRepository
            .findActiveById(userId)
            .orElseThrow(() -> BusinessException.notFound("User", userId));

    // Security check: must be admin to create pages
    securityService.requireAdmin(userId, teamSlug);

    // Check max pages limit
    long currentCount = teamPageRepository.countAdditionalPages(team.getId());
    if (currentCount >= MAX_ADDITIONAL_PAGES) {
      throw BusinessException.validation(
          "Maximum number of additional pages (" + MAX_ADDITIONAL_PAGES + ") reached");
    }

    validateVisibility(request, team);

    // Generate slug from title
    String slug =
        slugService.generateSlug(
            request.title(), s -> teamPageRepository.existsByTeamAndSlug(team.getId(), s));

    int order = teamPageRepository.getNextPageOrder(team.getId());

    TeamPage page =
        TeamPage.createAdditionalPage(
            creator, team, request.title(), slug, request.visibility(), order);

    teamPageRepository.persistAndFlush(page);

    updateMedia(page, request.media());

    teamPageRepository.persist(page);

    LOG.infov("Page '{0}' created by user {1} for team {2}", page.getName(), userId, teamSlug);
    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public TeamPageDto updatePage(
      String teamSlug, String pageSlug, TeamPageRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), pageSlug)
            .orElseThrow(() -> BusinessException.notFound("Page", pageSlug));

    // Security check: must be admin to update pages
    securityService.requireAdmin(userId, teamSlug);

    // Don't allow editing the about page through this endpoint
    if (page.isAboutPage()) {
      throw BusinessException.validation("About page must be edited through team settings");
    }

    validateVisibility(request, team);

    page.setName(request.title());
    page.setVisibility(request.visibility());

    updateMedia(page, request.media());

    teamPageRepository.persist(page);

    LOG.infov("Page {0} updated by user {1}", pageSlug, userId);
    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public void deletePage(String teamSlug, String pageSlug, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), pageSlug)
            .orElseThrow(() -> BusinessException.notFound("Page", pageSlug));

    // Security check: must be admin to delete pages
    securityService.requireAdmin(userId, teamSlug);

    // Don't allow deleting the about page
    if (page.isAboutPage()) {
      throw BusinessException.validation("Cannot delete the about page");
    }

    page.setDeleted(true);
    teamPageRepository.persist(page);
    LOG.infov("Page {0} deleted by user {1}", pageSlug, userId);
  }

  @Transactional
  public List<TeamPageSummaryDto> reorderPages(
      String teamSlug, ReorderPagesRequest request, Long userId) {
    Team team =
        teamRepository
            .findBySlug(teamSlug)
            .orElseThrow(() -> BusinessException.notFound("Team", teamSlug));

    // Security check: must be admin to reorder pages
    securityService.requireAdmin(userId, teamSlug);

    List<String> pageIds = request.pageIds();
    for (int i = 0; i < pageIds.size(); i++) {
      Long pageId = TsidUtils.toLong(pageIds.get(i));
      TeamPage page = teamPageRepository.findById(pageId);
      if (page == null || !page.getTeam().getId().equals(team.getId()) || page.isAboutPage()) {
        throw BusinessException.validation("Invalid page ID: " + pageIds.get(i));
      }
      page.setPageOrder(i);
      teamPageRepository.persist(page);
    }

    LOG.infov("Pages reordered by user {0} for team {1}", userId, teamSlug);
    return listPages(teamSlug, userId);
  }

  private boolean canViewPage(TeamPage page, boolean isMember) {
    // Team-only pages require membership
    if (page.getVisibility() == Visibility.TEAM) {
      return isMember;
    }
    return true;
  }
}
