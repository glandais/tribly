package com.tribly.service.config;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.dto.config.ConfigDto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ConfigServiceTest {

  @Inject ConfigService configService;

  @Test
  void getConfig_shouldReturnConfigDto() {
    ConfigDto config = configService.getConfig();

    assertNotNull(config);
    assertNotNull(config.keycloak());
    assertNotNull(config.keycloak().url());
    assertNotNull(config.keycloak().realm());
    assertNotNull(config.keycloak().clientId());
  }
}
