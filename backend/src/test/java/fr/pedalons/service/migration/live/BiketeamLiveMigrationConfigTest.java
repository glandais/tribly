package fr.pedalons.service.migration.live;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * {@code stuck-after} must exceed the longest silence of a live job, or a slow but healthy job is
 * requeued under its own worker — and ends {@code WORKER_LOST}.
 */
class BiketeamLiveMigrationConfigTest {

  /** Enabled, with the defaults of application.properties. */
  private static BiketeamLiveMigrationConfig config() {
    BiketeamLiveMigrationConfig config = new BiketeamLiveMigrationConfig();
    config.requestKey = Optional.of(BiketeamTestTokens.KEY_BASE64);
    config.triggerSecret = Optional.of("trigger");
    config.exportUrl = Optional.of("http://127.0.0.1:9");
    config.exportSecret = Optional.of("export");
    config.publicUrl = Optional.of("https://biketeam.example");
    config.grantTtl = Duration.ofMinutes(10);
    config.maxAttempts = 3;
    config.stuckAfter = Duration.ofMinutes(20);
    config.exportIdleTimeout = Duration.ofSeconds(60);
    config.exportTransferTimeout = Duration.ofMinutes(10);
    config.exportSnapshotTimeout = Duration.ofMinutes(5);
    return config;
  }

  @Test
  void theDefaults_start() {
    BiketeamLiveMigrationConfig config = config();
    assertDoesNotThrow(() -> config.validate(null));
    // A file's headers, an idle read, a heartbeat interval, a route's transaction, the margin.
    assertEquals(
        Duration.ofSeconds(120 + 60 + 60 + 600)
            .plus(BiketeamLiveMigrationConfig.STUCK_AFTER_MARGIN),
        config.minimumStuckAfter());
    assertTrue(config.minimumStuckAfter().compareTo(config.stuckAfter()) < 0);
  }

  @Test
  void aStuckAfterBelowTheLongestSilence_failsTheStartup() {
    BiketeamLiveMigrationConfig config = config();
    config.stuckAfter = Duration.ofMinutes(12);
    IllegalStateException e =
        assertThrows(IllegalStateException.class, () -> config.validate(null));
    assertTrue(e.getMessage().contains("stuck-after"), e.getMessage());
  }

  @Test
  void aLongSnapshotTimeout_raisesTheMinimum() {
    BiketeamLiveMigrationConfig config = config();
    config.exportSnapshotTimeout = Duration.ofMinutes(30);
    assertEquals(
        Duration.ofMinutes(30)
            .plusSeconds(60 + 60)
            .plus(BiketeamLiveMigrationConfig.STUCK_AFTER_MARGIN),
        config.minimumStuckAfter());
    assertThrows(IllegalStateException.class, () -> config.validate(null));
  }

  @Test
  void theTransferTimeout_isNotBoundByStuckAfter_theBodyWritesTheHeartbeat() {
    BiketeamLiveMigrationConfig config = config();
    config.exportTransferTimeout = Duration.ofHours(1);
    assertDoesNotThrow(() -> config.validate(null));
  }

  @Test
  void disabled_isNotChecked() {
    BiketeamLiveMigrationConfig config = config();
    config.requestKey = Optional.empty();
    config.triggerSecret = Optional.empty();
    config.exportUrl = Optional.empty();
    config.exportSecret = Optional.empty();
    config.publicUrl = Optional.empty();
    config.stuckAfter = Duration.ofMinutes(1);
    assertDoesNotThrow(() -> config.validate(null));
  }
}
