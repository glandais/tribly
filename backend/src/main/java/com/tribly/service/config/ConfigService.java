package com.tribly.service.config;

import com.tribly.dto.config.ConfigDto;
import com.tribly.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ConfigService {

  @ConfigProperty(name = "tribly.auth.webauthn.rp-id")
  String webAuthnRpId;

  @ConfigProperty(name = "tribly.app-name", defaultValue = "Tribly")
  String appName;

  @Public
  public ConfigDto getConfig() {
    return new ConfigDto(webAuthnRpId, appName);
  }
}
