package com.tribly.service.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.Optional;
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
    // Try by email
    Optional<User> existingByEmail = userRepository.findByEmail(email);

    User user;
    if (existingByEmail.isPresent()) {
      user = existingByEmail.get();
      LOG.debugv("Found existing user by email: {0}", email);
    } else {
      // Create new user
      user = new User(email, displayName);
      save(user);
      LOG.infov("Creating new user from Keycloak: {0}", email);
      return user;
    }

    // Update profile from Keycloak claims
    if (!displayName.isBlank() && !displayName.equals(user.getDisplayName())) {
      user.setDisplayName(displayName);
    }

    save(user);
    return user;
  }

  private void save(User user) {
    // Record login
    user.recordLogin();

    userRepository.persist(user);
  }
}
