package fr.pedalons.service.config;

import fr.pedalons.dto.config.ConfigDto;
import fr.pedalons.repository.team.TeamRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.ResolvedSite;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class ConfigService {

  @Inject DomainResolver domainResolver;

  @Inject TeamRepository teamRepository;

  @Public
  public ConfigDto getConfig() {
    ResolvedSite site = domainResolver.getResolvedSite();
    return new ConfigDto(
        site.effectiveHost(), site.effectiveName(), site.singleTeam(), pinnedTeamSlug(site));
  }

  private @Nullable String pinnedTeamSlug(ResolvedSite site) {
    Long pinnedTeamId = site.pinnedTeamId();
    if (pinnedTeamId == null) {
      return null;
    }
    // Guard against a soft-deleted pinned team: fall back to no pinning.
    return teamRepository.findActiveById(pinnedTeamId).map(t -> t.getSlug()).orElse(null);
  }
}
