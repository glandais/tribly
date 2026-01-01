package com.tribly.service.auth;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.util.TestDataCleaner;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserSyncServiceTest {

  @Inject UserSyncService userSyncService;
  @Inject UserRepository userRepository;
  @Inject TestDataCleaner dataCleaner;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
  }

  @Test
  void syncUser_shouldCreateNewUser() {
    User result = userSyncService.syncUser("newuser@example.com", "New User");

    assertNotNull(result);
    assertNotNull(result.getId());
    assertEquals("newuser@example.com", result.getEmail());
    assertEquals("New User", result.getDisplayName());
    assertNotNull(result.getLastLoginAt());
  }

  @Test
  void syncUser_shouldUpdateExistingUser() {
    User existing = userSyncService.syncUser("existing@example.com", "Original Name");
    Instant firstLogin = existing.getLastLoginAt();

    // Wait a moment to ensure timestamp changes
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    User result = userSyncService.syncUser("existing@example.com", "Updated Name");

    assertEquals(existing.getId(), result.getId());
    assertEquals("Updated Name", result.getDisplayName());
    assertTrue(result.getLastLoginAt().isAfter(firstLogin));
  }

  @Test
  void syncUser_shouldRecordLogin() {
    Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);

    User result = userSyncService.syncUser("user@example.com", "User");

    assertNotNull(result.getLastLoginAt());
    assertTrue(result.getLastLoginAt().isAfter(before));
  }

  @Test
  void syncUser_shouldNotUpdateDisplayNameWhenBlank() {
    User existing = userSyncService.syncUser("user@example.com", "Original Name");

    User result = userSyncService.syncUser("user@example.com", "");

    assertEquals(existing.getId(), result.getId());
    assertEquals("Original Name", result.getDisplayName());
  }

  @Test
  void syncUser_shouldHandleMultipleSyncsForSameUser() {
    User first = userSyncService.syncUser("user@example.com", "Name 1");
    User second = userSyncService.syncUser("user@example.com", "Name 2");
    User third = userSyncService.syncUser("user@example.com", "Name 3");

    assertEquals(first.getId(), second.getId());
    assertEquals(second.getId(), third.getId());
    assertEquals("Name 3", third.getDisplayName());
  }

  @Test
  void syncUser_shouldPersistToDatabase() {
    User result = userSyncService.syncUser("persist@example.com", "Persist User");

    User found = userRepository.findByEmail("persist@example.com").orElseThrow();
    assertEquals(result.getId(), found.getId());
    assertEquals("Persist User", found.getDisplayName());
  }

  @Test
  void syncUser_shouldNotSaveWhenDisplayNameIsSame() {
    User existing = userSyncService.syncUser("user@example.com", "Same Name");
    Instant firstLogin = existing.getLastLoginAt();

    // Wait a moment to ensure timestamp would change if saved
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    User result = userSyncService.syncUser("user@example.com", "Same Name");

    assertEquals(existing.getId(), result.getId());
    assertNotEquals(existing.getVersion(), result.getVersion());
    assertEquals("Same Name", result.getDisplayName());
    // lastLoginAt should not change because save() was not called
    assertNotEquals(firstLogin, result.getLastLoginAt());
  }

  @Test
  void syncUser_shouldRecoverFromConcurrentInserts() throws Exception {
    // Simulate race condition: multiple threads try to create the same user concurrently
    // Some will hit constraint violation and should recover by fetching the existing user
    String email = "concurrent-insert@example.com";
    int threadCount = 5;

    try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
      List<Future<User>> futures = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
        futures.add(executor.submit(() -> userSyncService.syncUser(email, "Concurrent User")));
      }

      Set<Long> userIds = new HashSet<>();
      for (Future<User> future : futures) {
        User user = future.get();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        userIds.add(user.getId());
      }

      // All calls should return the same user (only one should be created)
      assertEquals(1, userIds.size());

      executor.shutdown();
    }
  }

  @Test
  void syncUser_shouldRecoverFromConcurrentUpdates() throws Exception {
    // First create the user
    String email = "concurrent-update@example.com";
    userSyncService.syncUser(email, "Original Name");

    // Simulate race condition: multiple threads try to update the same user concurrently
    // Some will hit optimistic lock exception and should recover by fetching the current user
    int threadCount = 5;

    try (ExecutorService executor = Executors.newFixedThreadPool(threadCount)) {
      List<Future<User>> futures = new ArrayList<>();

      for (int i = 0; i < threadCount; i++) {
        final int index = i;
        futures.add(
            executor.submit(() -> userSyncService.syncUser(email, "Updated Name " + index)));
      }

      Set<Long> userIds = new HashSet<>();
      for (Future<User> future : futures) {
        User user = future.get();
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals(email, user.getEmail());
        userIds.add(user.getId());
      }

      // All calls should return the same user
      assertEquals(1, userIds.size());

      executor.shutdown();
    }
  }
}
