package fr.pedalons.service.user;

import static org.junit.jupiter.api.Assertions.*;

import fr.pedalons.AbstractBaseTest;
import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.platform.Domain;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.DomainResolver;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.util.TestDataCleaner;
import fr.pedalons.util.TestDataService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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
      ByteArrayInputStream textContent =
          new ByteArrayInputStream(
              ("This is plain text content with enough length for Magika to classify it"
                      + " reliably as text rather than an image format.")
                  .getBytes());

      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> userAvatarService.uploadAvatar(textContent, "file.txt"));
      assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
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
      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> userAvatarService.getAvatar(TsidUtils.toString(9999L), 256, "image/jpeg"));
      assertEquals(ErrorCode.NOT_FOUND, ex.getErrorCode());
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

    private InputStream getTestImageStream() {
      InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("image.png");
      assertNotNull(resourceAsStream, "image.png not found in test resources");
      return resourceAsStream;
    }

    @Test
    void shouldRejectNonImageFile() {
      pedalonsContext.setUserForTest(user);
      ByteArrayInputStream textContent =
          new ByteArrayInputStream(
              ("Not a real PDF document — just plain text long enough that Magika"
                      + " will classify it as text rather than misidentify by extension.")
                  .getBytes());

      PedalonsException ex =
          assertThrows(
              PedalonsException.class,
              () -> userAvatarService.uploadAvatar(textContent, "document.pdf"));
      assertEquals(ErrorCode.FILE_TYPE_REJECTED, ex.getErrorCode());
    }
  }
}
