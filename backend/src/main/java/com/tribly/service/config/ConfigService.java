package com.tribly.service.config;

import com.tribly.dto.config.ConfigDto;
import com.tribly.dto.config.KeycloakConfig;
import com.tribly.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ConfigService {

  @ConfigProperty(name = "tribly.keycloak.url")
  String keycloakUrl = "";

  @ConfigProperty(name = "tribly.keycloak.realm")
  String keycloakRealm = "";

  @ConfigProperty(name = "tribly.keycloak.client-id")
  String keycloakClientId = "";

  @Public
  public ConfigDto getConfig() {
    return new ConfigDto(new KeycloakConfig(keycloakUrl, keycloakRealm, keycloakClientId));
  }
}
