package com.tribly.service.common;

import com.tribly.common.exception.BusinessException;
import com.tribly.common.exception.ConflictException;
import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.TeamEntitySlugRedirect;
import com.tribly.domain.team.Team;
import com.tribly.dto.common.asset.MediaDto;
import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.error.ErrorCode;
import com.tribly.enums.TeamEntityType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.*;
import com.tribly.repository.common.TeamEntityRepository;
import com.tribly.service.asset.AssetService;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.service.team.TeamService;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import org.jboss.logging.Logger;

public abstract class TeamEntityService<
    T extends TeamEntity, R extends TeamEntityRepository<T, ?>, D> {

  private static final Logger LOG = Logger.getLogger(TeamEntityService.class);

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  @Inject protected TeamService teamService;

  @Inject protected TriblyQueryContext triblyContext;

  protected abstract R getRepository();

  protected abstract D toDto(T entity);

  protected D getDto(Team team, String entitySlug) {
    T entity = findBySlug(team, entitySlug);
    return toDto(entity);
  }

  protected void updateMedia(TeamEntity teamEntity, @Valid MediaDto mediaDto) {
    assetService.updateAssets(teamEntity, mediaDto);
  }

  protected void validateVisibility(Team team, WithVisibility request) {
    // Validate visibility: private teams can only have team-only items
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw new BusinessException(ErrorCode.INVALID_VISIBILITY);
    }
  }

  protected T findBySlug(Team team, String entitySlug) {
    Long domainId = triblyContext.getDomainId();
    Long userId = triblyContext.getUserIdNullable();
    Optional<T> byTeamAndSlug =
        getRepository().findByTeamAndSlug(domainId, team.getId(), userId, entitySlug);
    return byTeamAndSlug.orElseGet(
        () ->
            slugService
                .resolveEntityRedirect(team.getId(), getRepository().getEntityType(), entitySlug)
                .map(TeamEntitySlugRedirect::getEntityId)
                .flatMap(id -> getRepository().findByTeamAndId(domainId, team.getId(), userId, id))
                .orElseThrow(
                    () -> new NotFoundException(getRepository().getAllEntityType(), entitySlug)));
  }

  @Transactional
  protected D updateSlug(Team team, String slug, String newSlug) {
    T entity = findBySlug(team, slug);

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

    slugService.clearEntityRedirect(entity.getTeam().getId(), TeamEntityType.AD, newSlug);
    slugService.createEntityRedirect(entity, oldSlug);

    entity.setSlug(newSlug);
    getRepository().persist(entity);

    LOG.infov(
        "Slug changed from {0} to {1} by user {2}",
        oldSlug, newSlug, triblyContext.getUserIdNullable());
    return toDto(entity);
  }
}
