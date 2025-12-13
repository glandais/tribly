package com.tribly.service.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.UserRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.Optional;

/**
 * Service responsible for synchronizing users from Keycloak to the local database.
 * This ensures that users authenticated via Keycloak have corresponding records
 * in the application database for storing app-specific data.
 */
@ApplicationScoped
public class UserSyncService {

    private static final Logger LOG = Logger.getLogger(UserSyncService.class);

    @Inject
    UserRepository userRepository;

    /**
     * Sync user from Keycloak claims to local database.
     * Creates new user if not exists, updates existing user's profile.
     *
     * @param keycloakId  The Keycloak user ID (sub claim)
     * @param email       User's email from Keycloak
     * @param displayName User's display name (from name claim or constructed from given/family name)
     * @param avatarUrl   User's avatar URL (from Strava profile)
     * @param stravaId    User's Strava ID (from identity provider)
     * @return The synced User entity
     */
    @Transactional
    public User syncUser(String keycloakId, String email, String displayName,
                         String avatarUrl, String stravaId) {

        // First, try to find by Strava ID (for existing users or those logged in via Strava)
        Optional<User> existingByStrava = Optional.empty();
        if (stravaId != null && !stravaId.isBlank()) {
            existingByStrava = userRepository.findByStravaId(stravaId);
        }

        // Then try by email
        Optional<User> existingByEmail = userRepository.findByEmail(email);

        User user;
        if (existingByStrava.isPresent()) {
            user = existingByStrava.get();
            LOG.debugv("Found existing user by Strava ID: {0}", stravaId);
        } else if (existingByEmail.isPresent()) {
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
        if (avatarUrl != null && !avatarUrl.isBlank()) {
            user.setAvatarUrl(avatarUrl);
        }
        if (stravaId != null && !stravaId.isBlank() && user.getStravaId() == null) {
            // Only set Strava ID if not already set (avoid overwriting)
            user.setStravaId(stravaId);
        }

        // Record login
        user.recordLogin();

        userRepository.persist(user);
        return user;
    }

    /**
     * Find existing user by Strava ID or email.
     *
     * @param email    User's email
     * @param stravaId User's Strava ID
     * @return Optional containing the user if found
     */
    public Optional<User> findExistingUser(String email, String stravaId) {
        if (stravaId != null && !stravaId.isBlank()) {
            Optional<User> byStrava = userRepository.findByStravaId(stravaId);
            if (byStrava.isPresent()) {
                return byStrava;
            }
        }
        return userRepository.findByEmail(email);
    }
}
