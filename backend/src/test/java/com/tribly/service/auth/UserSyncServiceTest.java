package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.dto.users.response.UserDto;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserSyncServiceTest {

  @Inject UserSyncService userSyncService;
  @Inject UserRepository userRepository;
  @Inject TestDataCleaner dataCleaner;
  @Inject TestDataService dataService;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void syncUser_shouldCreateUserWhenNotInDatabase() {
    // Given: a JWT for a user that doesn't exist in the database
    JsonWebToken jwt = mock(JsonWebToken.class);
    when(jwt.getClaim("email")).thenReturn("newuser@example.com");
    when(jwt.getClaim("name")).thenReturn("New User");

    // When: syncUser is called with null userId (user not yet synced)
    UserDto result = userSyncService.syncUser(null, jwt);

    // Then: a new user should be created
    assertNotNull(result);
    assertEquals("newuser@example.com", result.email());
    assertEquals("New User", result.displayName());

    // Verify user was persisted in database
    User persistedUser =
        userRepository.findByEmail("newuser@example.com").orElseThrow(AssertionError::new);
    assertNotNull(persistedUser);
    assertEquals("New User", persistedUser.getDisplayName());
  }

  @Test
  void syncUser_shouldNotUpdateDisplayNameForExistingUser() {
    // Given: an existing user in the database with a specific display name
    User existingUser = dataService.createUser("existing@example.com", "Original Name");

    // And: a JWT with a different display name
    JsonWebToken jwt = mock(JsonWebToken.class);
    when(jwt.getClaim("name")).thenReturn("JWT Name");
    when(jwt.getClaim("given_name")).thenReturn("JWT");
    when(jwt.getClaim("family_name")).thenReturn("User");

    // When: syncUser is called for the existing user
    UserDto result = userSyncService.syncUser(existingUser.getId(), jwt);

    // Then: the display name should NOT be updated from JWT
    assertEquals("Original Name", result.displayName());

    // Verify in database
    User refreshedUser = userRepository.findById(existingUser.getId());
    assertEquals("Original Name", refreshedUser.getDisplayName());
  }

  @Test
  void syncUser_shouldUseGivenAndFamilyNameWhenNameNotPresent() {
    // Given: a JWT without "name" but with given_name and family_name
    JsonWebToken jwt = mock(JsonWebToken.class);
    when(jwt.getClaim("email")).thenReturn("parts@example.com");
    when(jwt.getClaim("name")).thenReturn(null);
    when(jwt.getClaim("given_name")).thenReturn("John");
    when(jwt.getClaim("family_name")).thenReturn("Doe");

    // When: syncUser is called
    UserDto result = userSyncService.syncUser(null, jwt);

    // Then: display name should be constructed from given_name + family_name
    assertEquals("John Doe", result.displayName());
  }

  @Test
  void syncUser_shouldUsePreferredUsernameAsFallback() {
    // Given: a JWT with only preferred_username
    JsonWebToken jwt = mock(JsonWebToken.class);
    when(jwt.getClaim("email")).thenReturn("fallback@example.com");
    when(jwt.getClaim("name")).thenReturn(null);
    when(jwt.getClaim("given_name")).thenReturn(null);
    when(jwt.getClaim("family_name")).thenReturn(null);
    when(jwt.getClaim("preferred_username")).thenReturn("johndoe");

    // When: syncUser is called
    UserDto result = userSyncService.syncUser(null, jwt);

    // Then: display name should use preferred_username
    assertEquals("johndoe", result.displayName());
  }
}
