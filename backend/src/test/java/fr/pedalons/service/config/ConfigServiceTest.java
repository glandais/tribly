package fr.pedalons.service.config;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.dto.config.ConfigDto;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ConfigServiceTest extends AbstractBaseTest {

  @Inject ConfigService configService;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject DomainResolver domainResolver;

  private Domain domain;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
  }

  @Test
  void getConfig_shouldReturnConfigDto() {
    ConfigDto config = configService.getConfig();

    assertNotNull(config);
    assertNotNull(config.webAuthnRpId());
    assertNotNull(config.appName());
  }
}
