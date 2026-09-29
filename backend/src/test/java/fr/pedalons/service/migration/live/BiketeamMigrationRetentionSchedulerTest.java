package fr.pedalons.service.migration.live;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.BiketeamMigrationStatus;
import fr.pedalons.repository.migration.BiketeamMigrationJobRepository;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * The one-year retention of biketeam transfer requests (privacy policy §6): docs/LEDGER_DONE.md
 * LEGAL-12.
 */
@QuarkusTest
class BiketeamMigrationRetentionSchedulerTest extends AbstractBaseTest {

  private static final Instant OVER_A_YEAR_AGO = Instant.now().minus(Duration.ofDays(400));
  private static final Instant RECENTLY = Instant.now().minus(Duration.ofDays(30));

  @Inject BiketeamMigrationRetentionScheduler scheduler;
  @Inject BiketeamMigrationJobRepository jobRepository;
  @Inject BiketeamTestData biketeamData;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    user = dataService.createVerifiedUser("migrator@example.com", "Migrator");
  }

  private long job(String biketeamTeamId, BiketeamMigrationStatus status, Instant finishedAt) {
    long id = biketeamData.createActiveJob(domain, user, biketeamTeamId).getId();
    biketeamData.end(id, status, finishedAt, OVER_A_YEAR_AGO);
    return id;
  }

  @Test
  void purgesRowsThatEndedOverAYearAgo() {
    job("1", BiketeamMigrationStatus.SUCCEEDED, OVER_A_YEAR_AGO);
    job("2", BiketeamMigrationStatus.FAILED, OVER_A_YEAR_AGO);
    // A lapsed grant never gets a finishedAt: its grant expiry is when it ended.
    job("3", BiketeamMigrationStatus.EXPIRED, null);
    long recent = job("4", BiketeamMigrationStatus.SUCCEEDED, RECENTLY);

    scheduler.purgeEndedMigrations();

    assertEquals(1, jobRepository.count());
    assertTrue(jobRepository.findByIdOptional(recent).isPresent());
  }

  @Test
  void neverPurgesAnActiveOrGrantedRow() {
    // Old timestamps on rows that have not ended: a stale finishedAt must not get them deleted.
    job("1", BiketeamMigrationStatus.QUEUED, OVER_A_YEAR_AGO);
    job("2", BiketeamMigrationStatus.RUNNING, null);
    job("3", BiketeamMigrationStatus.GRANTED, null);

    scheduler.purgeEndedMigrations();

    assertEquals(3, jobRepository.count());
  }
}
