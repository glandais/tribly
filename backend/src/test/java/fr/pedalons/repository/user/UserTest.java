package fr.pedalons.repository.user;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link User} utility methods.
 * No database required.
 */
class UserTest {

  private Domain testDomain;

  @BeforeEach
  void setUp() {
    testDomain = new Domain("localhost", "Test Domain", "http://localhost:5173");
  }

  @Nested
  @DisplayName("recordLogin")
  class RecordLogin {

    @Test
    @DisplayName("Should set lastLoginAt to current time")
    void recordLogin_shouldSetLastLoginAt() {
      User user = new User();
      assertNull(user.getLastLoginAt());

      Instant before = Instant.now();
      user.recordLogin();
      Instant after = Instant.now();

      assertNotNull(user.getLastLoginAt());
      assertTrue(
          !user.getLastLoginAt().isBefore(before) && !user.getLastLoginAt().isAfter(after),
          "lastLoginAt should be between before and after timestamps");
    }

    @Test
    @DisplayName("Should overwrite previous lastLoginAt")
    void recordLogin_shouldOverwritePrevious() {
      User user = new User();
      Instant oldLogin = Instant.now().minus(1, ChronoUnit.DAYS);
      user.setLastLoginAt(oldLogin);

      user.recordLogin();

      assertNotNull(user.getLastLoginAt());
      assertTrue(user.getLastLoginAt().isAfter(oldLogin));
    }

    @Test
    @DisplayName("Should be callable multiple times")
    void recordLogin_shouldBeCallableMultipleTimes() {
      User user = new User();

      user.recordLogin();
      Instant firstLogin = user.getLastLoginAt();

      // Small delay to ensure different timestamps
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }

      user.recordLogin();
      Instant secondLogin = user.getLastLoginAt();

      assertNotNull(firstLogin);
      assertNotNull(secondLogin);
      assertTrue(
          secondLogin.equals(firstLogin) || secondLogin.isAfter(firstLogin),
          "Second login should be at or after first login");
    }
  }

  @Nested
  @DisplayName("Constructor")
  class Constructor {

    @Test
    @DisplayName("Should set domain, email and displayName")
    void constructor_shouldSetDomainEmailAndDisplayName() {
      User user = new User(testDomain, "test@example.com", "Test User");

      assertSame(testDomain, user.getDomain());
      assertEquals("test@example.com", user.getEmail());
      assertEquals("Test User", user.getDisplayName());
    }

    @Test
    @DisplayName("Should set createdBy to self")
    void constructor_shouldSetCreatedByToSelf() {
      User user = new User(testDomain, "test@example.com", "Test User");

      assertSame(user, user.getCreatedBy());
    }

    @Test
    @DisplayName("Should have null lastLoginAt initially")
    void constructor_shouldHaveNullLastLoginAt() {
      User user = new User(testDomain, "test@example.com", "Test User");

      assertNull(user.getLastLoginAt());
    }

    @Test
    @DisplayName("Should have null avatarUrl initially")
    void constructor_shouldHaveNullAvatarUrl() {
      User user = new User(testDomain, "test@example.com", "Test User");

      assertNull(user.getAvatarUrl());
    }
  }
}
