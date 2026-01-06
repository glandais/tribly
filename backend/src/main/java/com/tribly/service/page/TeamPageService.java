package com.tribly.service.page;

import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.team.repository.TeamPageRepository;
import com.tribly.domain.user.User;
import com.tribly.dto.pages.request.ReorderPagesRequest;
import com.tribly.dto.pages.request.TeamPageRequest;
import com.tribly.dto.pages.response.TeamPageDto;
import com.tribly.dto.pages.response.TeamPageSummaryDto;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamPageService extends TeamEntityService<TeamPage> {

  private static final Logger LOG = Logger.getLogger(TeamPageService.class);
  private static final int MAX_ADDITIONAL_PAGES = 3;

  @Inject TeamPageRepository teamPageRepository;

  @Override
  protected EntityType getEntityType() {
    return EntityType.TEAM_PAGE;
  }

  @Override
  protected Optional<TeamPage> findByIdOptional(Long entityId) {
    return teamPageRepository.findByIdOptional(entityId);
  }

  @Override
  protected TeamPage getBySlug(Team team, String entitySlug, @Nullable User user) {
    // FIXME Query with security filter
    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), entitySlug)
            .orElseThrow(() -> BusinessException.notFound("Page", entitySlug));
    boolean isMember = securityService.isMember(user, team);
    if (!canViewPage(page, isMember)) {
      throw BusinessException.forbidden("Page");
    }
    return page;
  }

  public List<TeamPageSummaryDto> listPages(Team team, @Nullable User user) {
    boolean isMember = securityService.isMember(user, team);

    return team.getAdditionalPages().stream()
        .filter(page -> canViewPage(page, isMember))
        .map(TeamPageSummaryDto::from)
        .toList();
  }

  public TeamPageDto getPage(Team team, String pageSlug, @Nullable User user) {
    TeamPage page = get(team, pageSlug, user);

    // Check visibility
    boolean isMember = securityService.isMember(user, page.getTeam());
    if (!canViewPage(page, isMember)) {
      throw BusinessException.forbidden("You don't have permission to view this page");
    }

    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public TeamPageDto createPage(Team team, TeamPageRequest request, User creator) {
    // Security check: must be admin to create pages
    securityService.requireAdmin(creator, team);

    // Check max pages limit
    long currentCount = teamPageRepository.countAdditionalPages(team.getId());
    if (currentCount >= MAX_ADDITIONAL_PAGES) {
      throw BusinessException.validation(
          "Maximum number of additional pages (" + MAX_ADDITIONAL_PAGES + ") reached");
    }

    validateVisibility(request, team);

    // Generate slug from title
    String slug = slugService.generateSlug(request.title(), team.getId(), teamPageRepository);

    int order = teamPageRepository.getNextPageOrder(team.getId());

    TeamPage page =
        TeamPage.createAdditionalPage(
            creator, team, request.title(), slug, request.visibility(), order);

    teamPageRepository.persistAndFlush(page);

    updateMedia(page, request.media());

    teamPageRepository.persist(page);

    LOG.infov(
        "Page '{0}' created by user {1} for team {2}",
        page.getName(), creator.getId(), team.getSlug());
    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public TeamPageDto updatePage(Team team, String pageSlug, TeamPageRequest request, User user) {
    // Security check: must be admin to update pages
    securityService.requireAdmin(user, team);

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), pageSlug)
            .orElseThrow(() -> BusinessException.notFound("Page", pageSlug));

    // Don't allow editing the about page through this endpoint
    if (page.isAboutPage()) {
      throw BusinessException.validation("About page must be edited through team settings");
    }

    validateVisibility(request, team);

    page.setName(request.title());
    page.setVisibility(request.visibility());

    updateMedia(page, request.media());

    teamPageRepository.persist(page);

    LOG.infov("Page {0} updated by user {1}", pageSlug, user.getId());
    return TeamPageDto.from(page, assetService);
  }

  @Transactional
  public void deletePage(Team team, String pageSlug, User user) {
    // Security check: must be admin to delete pages
    securityService.requireAdmin(user, team);

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), pageSlug)
            .orElseThrow(() -> BusinessException.notFound("Page", pageSlug));

    // Don't allow deleting the about page
    if (page.isAboutPage()) {
      throw BusinessException.validation("Cannot delete the about page");
    }

    page.setDeleted(true);
    teamPageRepository.persist(page);
    LOG.infov("Page {0} deleted by user {1}", pageSlug, user.getId());
  }

  @Transactional
  public List<TeamPageSummaryDto> reorderPages(Team team, ReorderPagesRequest request, User user) {

    // Security check: must be admin to reorder pages
    securityService.requireAdmin(user, team);

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

    LOG.infov("Pages reordered by user {0} for team {1}", user.getId(), team.getSlug());
    return listPages(team, user);
  }

  private boolean canViewPage(TeamPage page, boolean isMember) {
    // Team-only pages require membership
    if (page.getVisibility() == Visibility.TEAM) {
      return isMember;
    }
    return true;
  }

  @Transactional
  public TeamPageDto updateSlug(Team team, String slugParam, String newSlug, User user) {
    securityService.requireAdmin(user, team);

    TeamPage page =
        teamPageRepository
            .findByTeamAndSlug(team.getId(), slugParam)
            .orElseThrow(() -> BusinessException.notFound("Page", slugParam));
    String currentSlug = page.getSlug();

    // Don't allow changing slug of the about page
    if (page.isAboutPage()) {
      throw BusinessException.validation("Cannot change the about page slug");
    }

    if (!slugService.isValidSlug(newSlug)) {
      throw BusinessException.validation("Invalid slug format");
    }

    if (currentSlug.equals(newSlug)) {
      return TeamPageDto.from(page, assetService);
    }

    if (teamPageRepository.existsByTeamAndSlug(team.getId(), newSlug)) {
      throw BusinessException.conflict("Slug already in use", "SLUG_TAKEN");
    }

    slugService.clearEntityRedirect(team.getId(), EntityType.TEAM_PAGE, newSlug);
    slugService.createEntityRedirect(page, currentSlug);

    page.setSlug(newSlug);
    teamPageRepository.persist(page);

    LOG.infov("Page slug changed from {0} to {1} by user {2}", currentSlug, newSlug, user.getId());
    return TeamPageDto.from(page, assetService);
  }
}
