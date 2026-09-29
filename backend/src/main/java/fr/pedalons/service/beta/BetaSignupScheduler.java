package fr.pedalons.service.beta;

import fr.pedalons.repository.beta.BetaSignupRepository;
import io.quarkus.logging.Log;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * Deletes beta sign-ups once past the retention the privacy policy announces (§6, one year): an
 * address that was never invited in a year is not going to be.
 */
@ApplicationScoped
public class BetaSignupScheduler {

  @Inject BetaSignupRepository betaSignupRepository;

  @ConfigProperty(name = "pedalons.beta.signups.retention-days", defaultValue = "365")
  int retentionDays;

  @Scheduled(cron = "0 45 3 * * ?")
  @Transactional
  void purgeOldSignups() {
    long purged =
        betaSignupRepository.deleteCreatedBefore(
            Instant.now().minus(Duration.ofDays(retentionDays)));
    if (purged > 0) {
      Log.infof("Beta sign-up purge: %d rows older than %d days deleted", purged, retentionDays);
    }
  }
}
