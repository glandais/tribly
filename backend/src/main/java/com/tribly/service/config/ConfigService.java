package com.tribly.service.config;

import com.tribly.dto.config.ConfigDto;
import com.tribly.dto.config.KeycloakConfig;
import com.tribly.dto.config.MapConfig;
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

  @ConfigProperty(name = "tribly.map.tile-url")
  String mapTileUrl = "";

  @ConfigProperty(name = "tribly.map.attribution")
  String mapAttribution = "";

  public ConfigDto getConfig() {
    return new ConfigDto(
        new KeycloakConfig(keycloakUrl, keycloakRealm, keycloakClientId),
        new MapConfig(mapTileUrl, mapAttribution));
  }
}
