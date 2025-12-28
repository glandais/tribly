package com.tribly.service.common;

import com.tribly.domain.common.TeamEntity;
import com.tribly.domain.team.repository.TeamRepository;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.common.response.MediaDto;
import com.tribly.service.asset.AssetService;
import com.tribly.service.security.TeamSecurityService;
import jakarta.inject.Inject;

public abstract class TeamEntityService {

  @Inject protected TeamSecurityService securityService;

  @Inject protected TeamRepository teamRepository;

  @Inject protected UserRepository userRepository;

  @Inject protected AssetService assetService;

  @Inject protected SlugService slugService;

  protected void updateMedia(TeamEntity teamEntity, MediaDto mediaDto) {
    teamEntity.setMarkdown(mediaDto.markdown());

    assetService.updateAssets(teamEntity, mediaDto.assets());
  }
}
