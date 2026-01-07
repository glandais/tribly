package com.tribly.service.page;

import com.tribly.domain.common.repository.TriblyPage;
import com.tribly.domain.team.Team;
import com.tribly.domain.team.TeamPage;
import com.tribly.domain.team.repository.TeamPageQuery;
import com.tribly.domain.team.repository.TeamPageRepository;
import com.tribly.domain.user.User;
import com.tribly.dto.error.ErrorCode;
import com.tribly.dto.pages.request.ReorderPagesRequest;
import com.tribly.dto.pages.request.TeamPageRequest;
import com.tribly.dto.pages.response.TeamPageDto;
import com.tribly.dto.pages.response.TeamPageSummaryDto;
import com.tribly.enums.ActionType;
import com.tribly.enums.AllEntityType;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.ForbiddenException;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.service.common.TeamEntityService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class TeamPageService extends TeamEntityService<TeamPage, TeamPageRepository, TeamPageDto> {

  private static final Logger LOG = Logger.getLogger(TeamPageService.class);
  private static final int MAX_ADDITIONAL_PAGES = 3;

  @Inject TeamPageRepository teamPageRepository;

  @Override
  protected TeamPageRepository getRepository() {
    return teamPageRepository;
  }

  @Override
  protected TeamPageDto toDto(TeamPage entity) {
    return TeamPageDto.from(entity, assetService);
  }

  @Override
  protected boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable TeamPage entity) {
    return switch (action) {
      case CREATE, UPDATE, DELETE -> securityService.getAdmin(user, team) != null;
      case JOIN -> false;
      // SQL
      case READ -> true;
    };
  }

  @Override
  protected TeamPage getBySlug(Team team, String entitySlug, @Nullable User user) {
    TriblyPage<TeamPage> teamPages =
        teamPageRepository.find(
            TeamPageQuery.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
                .slug(entitySlug)
                .page(0)
                .size(1)
                .build());
    if (teamPages.items().isEmpty()) {
      throw new NotFoundException(AllEntityType.TEAM_PAGE, entitySlug);
    } else {
      return teamPages.items().getFirst();
    }
  }

  public List<TeamPageSummaryDto> listPages(Team team, @Nullable User user) {
    TriblyPage<TeamPage> teamPages =
        teamPageRepository.find(
            TeamPageQuery.builder()
                .userId(user == null ? null : user.getId())
                .teamIds(Set.of(team.getId()))
                .includeAbout(false)
                .page(0)
                .size(100)
                .build());
    return teamPages.items().stream()
        .sorted(Comparator.comparing(t -> t.getPageOrder() == null ? 0 : t.getPageOrder()))
        .map(TeamPageSummaryDto::from)
        .toList();
  }

  @Transactional
  public TeamPageDto createPage(Team team, TeamPageRequest request, User creator) {
    hasRights(ActionType.CREATE, team, creator, null);

    // Check max pages limit
    long currentCount = teamPageRepository.countAdditionalPages(team.getId());
    if (currentCount >= MAX_ADDITIONAL_PAGES) {
      throw new BusinessException(ErrorCode.TOO_MANY_TEAM_PAGES);
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
    TeamPage page = get(ActionType.UPDATE, team, pageSlug, user);

    // Don't allow editing the about page through this endpoint
    if (page.isAboutPage()) {
      throw new ForbiddenException();
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
    TeamPage page = get(ActionType.DELETE, team, pageSlug, user);

    // Don't allow deleting the about page
    if (page.isAboutPage()) {
      throw new ForbiddenException();
    }

    page.setDeleted(true);
    teamPageRepository.persist(page);
    LOG.infov("Page {0} deleted by user {1}", pageSlug, user.getId());
  }

  @Transactional
  public List<TeamPageSummaryDto> reorderPages(Team team, ReorderPagesRequest request, User user) {
    hasRights(ActionType.CREATE, team, user, null);

    List<String> pageIds = request.pageIds();
    for (int i = 0; i < pageIds.size(); i++) {
      Long pageId = TsidUtils.toLong(pageIds.get(i));
      Optional<TeamPage> pageOptional = teamPageRepository.findByIdOptional(pageId);
      if (pageOptional.isPresent()) {
        TeamPage page = pageOptional.get();
        if (!page.getTeam().getId().equals(team.getId()) || page.isAboutPage()) {
          throw new BusinessException(ErrorCode.UNKNOWN);
        }
        page.setPageOrder(i);
        teamPageRepository.persist(page);
      } else {
        throw new NotFoundException(AllEntityType.TEAM_PAGE, pageId);
      }
    }

    LOG.infov("Pages reordered by user {0} for team {1}", user.getId(), team.getSlug());
    return listPages(team, user);
  }
}
