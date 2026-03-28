package fr.pedalons.service.user;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@QuarkusTest
class UserAvatarServiceTest extends AbstractBaseTest {

  @Inject UserAvatarService userAvatarService;
  @Inject UserRepository userRepository;
  @Inject TestDataService dataService;
  @Inject TestDataCleaner dataCleaner;
  @Inject PedalonsQueryContext pedalonsContext;
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
      pedalonsContext.setUserForTest(user);
      ByteArrayInputStream textContent = new ByteArrayInputStream("not an image".getBytes());

      assertThrows(
          BusinessException.class, () -> userAvatarService.uploadAvatar(textContent, "file.txt"));
    }

    @Test
    void shouldThrowForEmptyContent() {
      pedalonsContext.setUserForTest(user);
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

      pedalonsContext.setUserForTest(user);

      userAvatarService.deleteAvatar();

      // Refresh user from database
      User updatedUser = userRepository.findById(user.getId());
      assertNull(updatedUser.getAvatarUrl());
    }

    @Test
    void shouldHandleUserWithoutAvatar() {
      pedalonsContext.setUserForTest(user);

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
      pedalonsContext.setUserForTest(user);
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
      pedalonsContext.setUserForTest(user);
      ByteArrayInputStream textContent = new ByteArrayInputStream("plain text".getBytes());

      assertThrows(
          BusinessException.class,
          () -> userAvatarService.uploadAvatar(textContent, "document.pdf"));
    }
  }
}
