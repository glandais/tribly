package fr.pedalons.service.config;

import fr.pedalons.domain.platform.Domain;
import fr.pedalons.dto.config.ConfigDto;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfigService {

  @Inject DomainResolver domainResolver;

  @Public
  public ConfigDto getConfig() {
    Domain domain = domainResolver.getDomain();
    return new ConfigDto(domain.getDomain(), domain.getName(), domain.isSingleTeam());
  }
}
