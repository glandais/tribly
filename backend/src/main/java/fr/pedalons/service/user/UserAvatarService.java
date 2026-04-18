package fr.pedalons.service.user;

import fr.pedalons.common.TsidUtils;
import fr.pedalons.common.exception.BusinessException;
import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.common.exception.PedalonsException;
import fr.pedalons.domain.user.User;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.enums.AssetType;
import fr.pedalons.infrastructure.filetype.DetectedFileType;
import fr.pedalons.infrastructure.filetype.FileTypeDetector;
import fr.pedalons.infrastructure.imgproxy.ImgProxyService;
import fr.pedalons.infrastructure.storage.StorageService;
import fr.pedalons.repository.user.UserRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import fr.pedalons.service.security.annotation.Public;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.jboss.logging.Logger;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class UserAvatarService {

  private static final Logger LOG = Logger.getLogger(UserAvatarService.class);

  private static final int AVATAR_SIZE = 256;
  private static final String AVATARS_PREFIX = "avatars";

  @Inject UserRepository userRepository;

  @Inject ImgProxyService imgProxyService;

  @Inject PedalonsQueryContext pedalonsContext;

  @Inject StorageService storageService;

  @Inject FileTypeDetector fileTypeDetector;

  @Logged
  @Transactional
  public void uploadAvatar(InputStream content, String fileName) throws IOException {
    User user = pedalonsContext.getUser();

    // Create temp file for content type validation
    long tempFileId = TSID.Factory.getTsid().toLong();
    File tempFile = createTempFile(tempFileId);

    // Save original file temporarily
    Files.copy(content, tempFile.toPath());

    DetectedFileType detected;
    try {
      detected = fileTypeDetector.detectAndValidate(tempFile, fileName, AssetType.IMAGE);
    } catch (PedalonsException e) {
      tempFile.delete();
      throw e;
    } catch (RuntimeException e) {
      tempFile.delete();
      throw new BusinessException(ErrorCode.INVALID_FORMAT, e);
    }
    String contentType = detected.mimeType();
    if (!contentType.startsWith("image/")) {
      tempFile.delete();
      throw new BusinessException(ErrorCode.INVALID_FORMAT);
    }

    // Upload temp file to S3 for imgproxy processing
    String tempKey = getAvatarKey(tempFileId);
    try (InputStream fis = new FileInputStream(tempFile)) {
      storageService.store(tempKey, fis, contentType, tempFile.length());
    }

    // Generate final file ID and resize via imgproxy
    long fileId = TSID.Factory.getTsid().toLong();
    File avatarFile = createTempFile(fileId);

    // Use ImgProxy to resize to 256x256
    try (InputStream resizedStream =
        imgProxyService.getPhotoContent("image/jpeg", tempKey, AVATAR_SIZE, AVATAR_SIZE)) {
      Files.copy(resizedStream, avatarFile.toPath());
    } catch (Exception e) {
      tempFile.delete();
      storageService.delete(tempKey);
      throw new BusinessException(ErrorCode.INVALID_FORMAT, e);
    }

    // Upload resized avatar to S3
    String avatarKey = getAvatarKey(fileId);
    Map<String, String> metadata =
        Map.of(
            "file-id", TsidUtils.toString(fileId),
            "user-id", TsidUtils.toString(user.getId()),
            "type", "avatar");
    try (InputStream fis = new FileInputStream(avatarFile)) {
      storageService.store(avatarKey, fis, "image/jpeg", avatarFile.length(), metadata);
    }

    // Delete temp files and temp S3 object
    tempFile.delete();
    avatarFile.delete();
    storageService.delete(tempKey);

    // Delete old avatar file if exists
    String oldAvatarUrl = user.getAvatarUrl();
    if (oldAvatarUrl != null) {
      deleteAvatarFromUrl(oldAvatarUrl);
    }

    // Update user with new avatar URL
    String avatarUrl =
        "/api/download/public/avatars/" + TsidUtils.toString(fileId) + "/" + AVATAR_SIZE;
    user.setAvatarUrl(avatarUrl);
    userRepository.persist(user);
  }

  @Logged
  @Transactional
  public void deleteAvatar() {
    User user = pedalonsContext.getUser();

    String avatarUrl = user.getAvatarUrl();
    if (avatarUrl != null) {
      deleteAvatarFromUrl(avatarUrl);
      user.setAvatarUrl(null);
      userRepository.persist(user);
    }
  }

  @Public
  public Response getAvatar(String fileId, int size, String accept) {
    // Limit size to AVATAR_SIZE (avatars are stored at 256x256)
    int effectiveSize = Math.min(size, AVATAR_SIZE);
    long fileIdLong = TsidUtils.toLong(fileId);

    String key = getAvatarKey(fileIdLong);

    // Verify file exists
    if (!storageService.exists(key)) {
      throw new NotFoundException(ErrorCode.NOT_FOUND);
    }

    return Response.fromResponse(
            imgProxyService.getPhoto(accept, key, effectiveSize, effectiveSize))
        .build();
  }

  private File createTempFile(long fileId) throws IOException {
    Path tempDir = Path.of(System.getProperty("java.io.tmpdir"), "pedalons-avatars");
    Files.createDirectories(tempDir);
    return new File(tempDir.toFile(), TsidUtils.toString(fileId));
  }

  private String getAvatarKey(long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return AVATARS_PREFIX + "/" + subPath + "/" + idString;
  }

  private void deleteAvatarFromUrl(@Nullable String avatarUrl) {
    if (avatarUrl == null) {
      return;
    }
    // Extract fileId from URL: /api/download/public/avatars/{fileId}/{size}
    String[] parts = avatarUrl.split("/");
    if (parts.length >= 6) {
      try {
        long fileId = TsidUtils.toLong(parts[5]);
        String key = getAvatarKey(fileId);
        storageService.delete(key);
      } catch (Exception e) {
        LOG.warnf(
            e, "Failed to delete old avatar from S3 (orphaned object) avatarUrl=%s", avatarUrl);
      }
    } else {
      LOG.warnf("Skipping avatar deletion: malformed avatar URL %s", avatarUrl);
    }
  }
}
