package fr.pedalons.service.auth;

import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.repository.auth.AuthTokenRepository;
import fr.pedalons.repository.gps.GpsOAuthStateRepository;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Scheduled job to clean up expired auth sessions and tokens.
 *
 * <p>Runs daily at 3 AM to remove:
 *
 * <ul>
 *   <li>Expired or revoked auth sessions
 *   <li>Expired or used auth tokens (email verification, magic links)
 *   <li>Expired GPS OAuth states
 * </ul>
 */
@ApplicationScoped
public class AuthCleanupScheduler {

  private static final Logger LOG = Logger.getLogger(AuthCleanupScheduler.class);

  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;
  @Inject GpsOAuthStateRepository gpsOAuthStateRepository;

  @Scheduled(cron = "0 0 3 * * ?") // Every day at 3 AM
  @Transactional
  void cleanupExpiredAuthData() {
    long deletedSessions = authSessionRepository.deleteExpiredSessions();
    long deletedTokens = authTokenRepository.deleteExpiredTokens();
    long deletedGpsStates = gpsOAuthStateRepository.deleteExpiredStates();

    if (deletedSessions > 0 || deletedTokens > 0 || deletedGpsStates > 0) {
      LOG.infof(
          "Auth cleanup completed: %d sessions deleted, %d tokens deleted, %d GPS OAuth states"
              + " deleted",
          deletedSessions, deletedTokens, deletedGpsStates);
    }
  }
}
