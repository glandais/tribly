package fr.pedalons.service.common;

import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.ConflictException;
import fr.pedalons.domain.common.TeamEntity;
import fr.pedalons.domain.common.TeamEntitySlugRedirect;
import fr.pedalons.domain.team.Team;
import fr.pedalons.dto.common.asset.MediaDto;
import fr.pedalons.dto.common.request.WithVisibility;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.Visibility;
import fr.pedalons.infrastructure.exception.*;
import fr.pedalons.repository.common.TeamEntityRepository;
import fr.pedalons.service.asset.AssetService;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.team.TeamService;
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

  @Inject protected PedalonsQueryContext pedalonsContext;

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
    Long domainId = pedalonsContext.getDomainId();
    Long userId = pedalonsContext.getUserIdNullable();
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

    slugService.clearEntityRedirect(
        entity.getTeam().getId(), getRepository().getEntityType(), newSlug);
    slugService.createEntityRedirect(entity, oldSlug);

    entity.setSlug(newSlug);
    getRepository().persist(entity);

    LOG.infov(
        "Slug changed from {0} to {1} by user {2}",
        oldSlug, newSlug, pedalonsContext.getUserIdNullable());
    return toDto(entity);
  }
}
