package com.tribly.service.common;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.TeamEntitySlugRedirect;
import com.tribly.domain.common.repository.TeamEntityRepository;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.dto.error.ErrorCode;
import com.tribly.enums.ActionType;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.*;
import com.tribly.service.asset.AssetService;
import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

public abstract class TeamEntityService<
    T extends TeamEntity, R extends TeamEntityRepository<T, ?>, D> {

  private static final Logger LOG = Logger.getLogger(TeamEntityService.class);

  @Inject protected TeamSecurityService securityService;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  protected abstract T getBySlug(Team team, String entitySlug, @Nullable User user);

  protected abstract R getRepository();

  protected abstract D toDto(T entity);

  protected abstract boolean hasRights(
      ActionType action, Team team, @Nullable User user, @Nullable T entity);

  protected void checkRights(
      ActionType action, Team team, @Nullable User user, @Nullable T entity) {
    if (!hasRights(action, team, user, entity)) {
      throw new ForbiddenException();
    }
  }

  public final D getDto(Team team, String adSlug, @Nullable User user) {
    T entity = get(ActionType.READ, team, adSlug, user);
    return toDto(entity);
  }

  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    teamEntity.setMarkdown(mediaDto.markdown());

    assetService.updateAssets(teamEntity, mediaDto.assets());
  }

  protected void validateVisibility(WithVisibility request, Team team) {
    // Validate visibility: private teams can only have team-only items
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw new BusinessException(ErrorCode.INVALID_VISIBILITY);
    }
  }

  public final T get(ActionType action, Team team, String entitySlug, @Nullable User user) {
    T entity = findBySlug(team, entitySlug, user);
    checkRights(action, team, user, entity);
    return entity;
  }

  private T findBySlug(Team team, String entitySlug, @Nullable User user) {
    try {
      return getBySlug(team, entitySlug, user);
    } catch (NotFoundException e) {
      // Check for redirect if not found
      Optional<TeamEntitySlugRedirect> redirect =
          slugService.resolveEntityRedirect(
              team.getId(), getRepository().getEntityType(), entitySlug);
      if (redirect.isPresent()) {
        String newSlug = getNewSlug(redirect.get());
        if (newSlug != null) {
          return getBySlug(team, newSlug, user);
        }
      }
      throw e;
    }
  }

  @Nullable
  private String getNewSlug(TeamEntitySlugRedirect redirect) {
    Optional<T> optionalEntity = getRepository().findByIdOptional(redirect.getEntityId());
    return optionalEntity
        .filter(entity -> !entity.isDeleted())
        .map(TeamEntity::getSlug)
        .orElse(null);
  }

  @Transactional
  public final D updateSlug(Team team, String slugParam, String newSlug, User user) {
    T entity = get(ActionType.UPDATE, team, slugParam, user);

    checkRights(ActionType.UPDATE, team, user, entity);

    String oldSlug = entity.getSlug();

    if (!slugService.isValidSlug(newSlug)) {
      throw new BusinessException(ErrorCode.INVALID_SLUG);
    }

    if (oldSlug.equals(newSlug)) {
      return toDto(entity);
    }

    if (getRepository().existsByTeamAndSlug(entity.getTeam().getId(), newSlug)) {
      throw new ConflictException(ErrorCode.SLUG_TAKEN);
    }

    slugService.clearEntityRedirect(entity.getTeam().getId(), EntityType.AD, newSlug);
    slugService.createEntityRedirect(entity, oldSlug);

    entity.setSlug(newSlug);
    getRepository().persist(entity);

    LOG.infov("Slug changed from {0} to {1} by user {2}", oldSlug, newSlug, user.getId());
    return toDto(entity);
  }
}
