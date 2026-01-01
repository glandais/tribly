package com.tribly.service.auth;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.users.response.UserDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.jspecify.annotations.Nullable;

/**
 * Service responsible for synchronizing users from Keycloak to the local database.
 * This ensures that users authenticated via Keycloak have corresponding records
 * in the application database for storing app-specific data.
 */
@ApplicationScoped
public class UserSyncService {

  @Inject UserRepository userRepository;

  @Transactional
  public UserDto syncUser(@Nullable Long userId, JsonWebToken jwt) {
    if (userId == null) {
      String email = jwt.getClaim("email");
      // User not synced yet - extract from JWT and sync
      String displayName = extractDisplayName(jwt);
      return createUser(email, displayName);
    } else {
      return updateUser(userId);
    }
  }

  private UserDto updateUser(Long userId) {
    User user = userRepository.findById(userId);
    user.recordLogin();
    userRepository.persistAndFlush(user);
    return UserDto.from(user);
  }

  private UserDto createUser(String email, String displayName) {
    User user;
    user = new User(email, displayName);
    user.recordLogin();
    userRepository.persistAndFlush(user);
    // FIXME create user private team
    return UserDto.from(user);
  }

  private String extractDisplayName(JsonWebToken jwt) {
    String displayName = jwt.getClaim("name");
    if (displayName == null || displayName.isBlank()) {
      String givenName = jwt.getClaim("given_name");
      String familyName = jwt.getClaim("family_name");
      if (givenName != null || familyName != null) {
        displayName =
            ((givenName != null ? givenName : "") + " " + (familyName != null ? familyName : ""))
                .trim();
      }
    }
    if (displayName == null || displayName.isBlank()) {
      String preferredUsername = jwt.getClaim("preferred_username");
      displayName = preferredUsername != null ? preferredUsername : jwt.getClaim("email");
    }
    return displayName;
  }
}
