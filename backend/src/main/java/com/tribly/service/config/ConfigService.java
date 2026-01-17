package com.tribly.service.config;

import com.tribly.domain.platform.Domain;
import com.tribly.dto.config.ConfigDto;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.annotation.Public;
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
