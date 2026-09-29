package fr.pedalons.service.beta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.repository.beta.BetaSignupRepository;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The one-year retention of beta sign-ups (privacy policy §6): docs/LEDGER_DONE.md LEGAL-12. */
@QuarkusTest
class BetaSignupSchedulerTest extends AbstractBaseTest {

  @Inject BetaSignupScheduler scheduler;
  @Inject BetaSignupRepository betaSignupRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void purgesSignupsOlderThanAYearOnly() {
    dataService.createBetaSignup("old@example.com", Instant.now().minus(Duration.ofDays(366)));
    dataService.createBetaSignup("recent@example.com", Instant.now().minus(Duration.ofDays(364)));

    scheduler.purgeOldSignups();

    assertEquals(1, betaSignupRepository.count());
    assertTrue(betaSignupRepository.existsByEmail("recent@example.com"));
  }
}
