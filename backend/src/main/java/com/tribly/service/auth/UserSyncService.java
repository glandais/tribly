package com.tribly.service.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.hibernate.StaleObjectStateException;
import org.jboss.logging.Logger;

/**
 * Service responsible for synchronizing users from Keycloak to the local database.
 * This ensures that users authenticated via Keycloak have corresponding records
 * in the application database for storing app-specific data.
 */
@ApplicationScoped
public class UserSyncService {

  private static final Logger LOG = Logger.getLogger(UserSyncService.class);

  @Inject UserRepository userRepository;

  /**
   * Sync user from Keycloak claims to local database.
   * Creates new user if not exists, updates existing user's profile.
   * Handles optimistic locking conflicts with retry.
   *
   * @param email       User's email from Keycloak
   * @param displayName User's display name (from name claim or constructed from given/family name)
   * @return The synced User entity
   */
  @Transactional
  public User syncUser(String email, String displayName) {
    int maxRetries = 3;
    int attempt = 0;

    while (attempt < maxRetries) {
      try {
        return syncUserInternal(email, displayName);
      } catch (StaleObjectStateException e) {
        attempt++;
        if (attempt >= maxRetries) {
          LOG.errorv(
              "Failed to sync user after {0} attempts due to optimistic locking conflict: {1}",
              maxRetries, email);
          throw e;
        }
        LOG.warnv(
            "Optimistic locking conflict on user sync attempt {0}/{1} for {2}, retrying...",
            attempt, maxRetries, email);
        // Clear the persistence context to avoid stale entities
        userRepository.getEntityManager().clear();
      }
    }

    throw new IllegalStateException("Should not reach here");
  }

  private User syncUserInternal(String email, String displayName) {
    // Try by email
    Optional<User> existingByEmail = userRepository.findByEmail(email);

    User user;
    if (existingByEmail.isPresent()) {
      user = existingByEmail.get();
      LOG.debugv("Found existing user by email: {0}", email);
    } else {
      // Create new user
      user = new User(email, displayName);
      LOG.infov("Creating new user from Keycloak: {0}", email);
    }

    // Update profile from Keycloak claims
    if (displayName != null && !displayName.isBlank()) {
      user.setDisplayName(displayName);
    }

    // Record login
    user.recordLogin();

    userRepository.persist(user);
    return user;
  }
}
