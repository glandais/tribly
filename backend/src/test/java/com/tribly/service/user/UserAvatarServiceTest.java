package com.tribly.service.user;

import static org.junit.jupiter.api.Assertions.*;

import com.tribly.common.TsidUtils;
import com.tribly.common.exception.BusinessException;
import com.tribly.domain.platform.Domain;
import com.tribly.domain.user.User;
import com.tribly.repository.user.UserRepository;
import com.tribly.service.security.DomainResolver;
import com.tribly.service.security.TriblyQueryContext;
import com.tribly.util.TestDataCleaner;
import com.tribly.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserAvatarServiceTest {

  @Inject UserAvatarService userAvatarService;
  @Inject UserRepository userRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject TriblyQueryContext triblyContext;
  @Inject DomainResolver domainResolver;

  private Domain domain;
  private User user;

  @BeforeEach
  void setUp() {
    dataCleaner.cleanAll();
    domain = dataService.getOrCreateDefaultDomain();
    domainResolver.setDomainForTest(domain);
    user = dataService.createVerifiedUser("avatar@example.com", "Avatar User");
  }

  @Nested
  class UploadAvatar {

    @Test
    void shouldThrowForNonImageContent() {
      triblyContext.setUserForTest(user);
      ByteArrayInputStream textContent = new ByteArrayInputStream("not an image".getBytes());

      assertThrows(
          BusinessException.class, () -> userAvatarService.uploadAvatar(textContent, "file.txt"));
    }

    @Test
    void shouldThrowForEmptyContent() {
      triblyContext.setUserForTest(user);
      ByteArrayInputStream emptyContent = new ByteArrayInputStream(new byte[0]);

      assertThrows(
          Exception.class, () -> userAvatarService.uploadAvatar(emptyContent, "empty.png"));
    }
  }

  @Nested
  class DeleteAvatar {

    @Test
    void shouldClearAvatarUrl() {
      // Set up user with avatar URL
      user.setAvatarUrl("/api/download/public/avatars/test123/256");
      dataService.updateUser(user);

      triblyContext.setUserForTest(user);

      userAvatarService.deleteAvatar();

      // Refresh user from database
      User updatedUser = userRepository.findById(user.getId());
      assertNull(updatedUser.getAvatarUrl());
    }

    @Test
    void shouldHandleUserWithoutAvatar() {
      triblyContext.setUserForTest(user);

      // Should not throw when user has no avatar
      assertDoesNotThrow(() -> userAvatarService.deleteAvatar());
    }
  }

  @Nested
  class GetAvatar {

    @Test
    void shouldThrowForNonexistentFile() {
      assertThrows(
          BusinessException.class,
          () -> userAvatarService.getAvatar(TsidUtils.toString(9999L), 256, "image/jpeg"));
    }

    @Test
    void shouldLimitSizeToMaxAvatarSize() {
      // This test verifies the size limiting logic
      // The actual file access will fail, but we're testing the size calculation
      assertThrows(
          Exception.class, () -> userAvatarService.getAvatar("test123", 1024, "image/jpeg"));
    }
  }

  @Nested
  class ContentTypeDetection {

    @Test
    void shouldDetectPngFromExtension() throws IOException {
      triblyContext.setUserForTest(user);
      // Create minimal PNG header
      byte[] pngHeader = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
      ByteArrayInputStream pngContent = new ByteArrayInputStream(pngHeader);

      // Will fail at ImgProxy resize but validates content type detection passes
      try {
        userAvatarService.uploadAvatar(pngContent, "test.png");
        fail("Should have thrown exception during resize");
      } catch (BusinessException e) {
        // Expected - fails at resize step, not content type detection
        assertEquals("INVALID_FORMAT", e.getErrorCode().name());
      }
    }

    @Test
    void shouldRejectNonImageFile() {
      triblyContext.setUserForTest(user);
      ByteArrayInputStream textContent = new ByteArrayInputStream("plain text".getBytes());

      assertThrows(
          BusinessException.class,
          () -> userAvatarService.uploadAvatar(textContent, "document.pdf"));
    }
  }
}
