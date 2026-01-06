package com.tribly.service.common;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.common.TeamEntitySlugRedirect;
import com.tribly.domain.team.Team;
import com.tribly.domain.user.User;
import com.tribly.dto.common.request.WithVisibility;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.enums.EntityType;
import com.tribly.enums.Visibility;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.exception.NotFoundException;
import com.tribly.service.asset.AssetService;
import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;
import java.util.Optional;
import org.jspecify.annotations.Nullable;

public abstract class TeamEntityService<T extends TeamEntity> {

  @Inject protected TeamSecurityService securityService;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    teamEntity.setMarkdown(mediaDto.markdown());

    assetService.updateAssets(teamEntity, mediaDto.assets());
  }

  protected void validateVisibility(WithVisibility request, Team team) {
    // Validate visibility: private teams can only have team-only items
    if (team.getVisibility() != Visibility.PUBLIC && request.visibility() == Visibility.PUBLIC) {
      throw BusinessException.validation("Private teams can only have team-only items");
    }
  }

  protected abstract T getBySlug(Team team, String entitySlug, @Nullable User user);

  protected abstract EntityType getEntityType();

  protected abstract Optional<T> findByIdOptional(Long entityId);

  public final T get(Team team, String entitySlug, @Nullable User user) {
    try {
      return getBySlug(team, entitySlug, user);
    } catch (NotFoundException e) {
      // Check for redirect if not found
      Optional<TeamEntitySlugRedirect> redirect =
          slugService.resolveEntityRedirect(team.getId(), getEntityType(), entitySlug);
      if (redirect.isPresent()) {
        String newSlug = getNewSlug(redirect.get());
        if (newSlug != null) {
          // recheck ad access
          return getBySlug(team, newSlug, user);
        }
      }
      throw e;
    }
  }

  @Nullable
  private String getNewSlug(TeamEntitySlugRedirect redirect) {
    Optional<T> ad = findByIdOptional(redirect.getEntityId());
    return ad.filter(a -> !a.isDeleted()).map(TeamEntity::getSlug).orElse(null);
  }
}
