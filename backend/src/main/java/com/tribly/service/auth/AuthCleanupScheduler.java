package com.tribly.service.auth;

import com.tribly.repository.auth.AuthSessionRepository;
import com.tribly.repository.auth.AuthTokenRepository;
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
 * </ul>
 */
@ApplicationScoped
public class AuthCleanupScheduler {

  private static final Logger LOG = Logger.getLogger(AuthCleanupScheduler.class);

  @Inject AuthSessionRepository authSessionRepository;
  @Inject AuthTokenRepository authTokenRepository;

  @Scheduled(cron = "0 0 3 * * ?") // Every day at 3 AM
  @Transactional
  void cleanupExpiredAuthData() {
    long deletedSessions = authSessionRepository.deleteExpiredSessions();
    long deletedTokens = authTokenRepository.deleteExpiredTokens();

    if (deletedSessions > 0 || deletedTokens > 0) {
      LOG.infof(
          "Auth cleanup completed: %d sessions deleted, %d tokens deleted",
          deletedSessions, deletedTokens);
    }
  }
}
