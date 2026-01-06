package com.tribly.service.user;

import com.tribly.domain.user.User;
import com.tribly.domain.user.repository.UserRepository;
import com.tribly.infrastructure.exception.BusinessException;
import com.tribly.infrastructure.id.TsidUtils;
import com.tribly.infrastructure.imgproxy.ImgProxyService;
import io.hypersistence.tsid.TSID;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jspecify.annotations.Nullable;

@ApplicationScoped
public class UserAvatarService {

  private static final int AVATAR_SIZE = 256;
  private static final String AVATARS_DIRECTORY = "avatars";

  @Inject UserRepository userRepository;

  @Inject ImgProxyService imgProxyService;

  @ConfigProperty(name = "storage.path")
  String storagePath = "/tmp";

  private final Tika tika = new Tika();

  @Transactional
  public void uploadAvatar(User user, InputStream content, String fileName) throws IOException {
    // Validate content type
    long tempFileId = TSID.Factory.getTsid().toLong();
    File tempFile = getAvatarFile(tempFileId);
    Files.createDirectories(tempFile.getParentFile().toPath());

    // Save original file temporarily
    try (FileOutputStream fos = new FileOutputStream(tempFile)) {
      IOUtils.copy(content, fos);
    }
    setFilePermissions(tempFile);

    String contentType = getContentType(tempFile, fileName);
    if (!contentType.startsWith("image/")) {
      tempFile.delete();
      throw BusinessException.validation("File must be an image");
    }

    // Generate final file ID and resize
    long fileId = TSID.Factory.getTsid().toLong();
    File avatarFile = getAvatarFile(fileId);
    Files.createDirectories(avatarFile.getParentFile().toPath());

    // Use ImgProxy to resize to 256x256
    try (InputStream resizedStream =
        imgProxyService.getPhotoContent(
            "image/jpeg", getRelativeAvatarPath(tempFileId), AVATAR_SIZE, AVATAR_SIZE)) {
      try (FileOutputStream fos = new FileOutputStream(avatarFile)) {
        IOUtils.copy(resizedStream, fos);
      }
    } catch (Exception e) {
      tempFile.delete();
      throw BusinessException.validation("Failed to process image");
    }

    // Delete temp file
    tempFile.delete();
    setFilePermissions(avatarFile);

    // Delete old avatar file if exists
    String oldAvatarUrl = user.getAvatarUrl();
    if (oldAvatarUrl != null) {
      deleteAvatarFile(oldAvatarUrl);
    }

    // Update user with new avatar URL
    String avatarUrl =
        "/api/download/public/avatars/" + TsidUtils.toString(fileId) + "/" + AVATAR_SIZE;
    user.setAvatarUrl(avatarUrl);
    userRepository.persist(user);
  }

  @Transactional
  public void deleteAvatar(User userParam) {
    User user =
        userRepository
            .findActiveById(userParam.getId())
            .orElseThrow(() -> BusinessException.notFound("User", userParam.getId()));

    String avatarUrl = user.getAvatarUrl();
    if (avatarUrl != null) {
      deleteAvatarFile(avatarUrl);
      user.setAvatarUrl(null);
      userRepository.persist(user);
    }
  }

  public Response getAvatar(String fileId, int size, String accept) {
    // Limit size to AVATAR_SIZE (avatars are stored at 256x256)
    int effectiveSize = Math.min(size, AVATAR_SIZE);
    long fileIdLong = TsidUtils.toLong(fileId);

    // Verify file exists
    File avatarFile = getAvatarFile(fileIdLong);
    if (!avatarFile.exists()) {
      throw BusinessException.notFound("Avatar", fileId);
    }

    String relativePath = getRelativeAvatarPath(fileIdLong);
    return Response.fromResponse(
            imgProxyService.getPhoto(accept, relativePath, effectiveSize, effectiveSize))
        .build();
  }

  private File getAvatarFile(long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    Path avatarDirectory = Path.of(storagePath, AVATARS_DIRECTORY, subPath);
    return new File(avatarDirectory.toFile(), idString);
  }

  private String getRelativeAvatarPath(long fileId) {
    String idString = TsidUtils.toString(fileId);
    String subPath = idString.substring(0, 4);
    return AVATARS_DIRECTORY + "/" + subPath + "/" + idString;
  }

  private void deleteAvatarFile(@Nullable String avatarUrl) {
    if (avatarUrl == null) {
      return;
    }
    // Extract fileId from URL: /api/users/avatars/{fileId}/{size}
    String[] parts = avatarUrl.split("/");
    if (parts.length >= 5) {
      try {
        long fileId = TsidUtils.toLong(parts[4]);
        File file = getAvatarFile(fileId);
        if (file.exists()) {
          file.delete();
        }
      } catch (Exception e) {
        // Ignore errors when deleting old avatar
      }
    }
  }

  private void setFilePermissions(File file) throws IOException {
    Set<PosixFilePermission> ownerWritable = PosixFilePermissions.fromString("rw-r--r--");
    Files.setPosixFilePermissions(file.toPath(), ownerWritable);
  }

  private String getContentType(File file, String fileName) {
    Metadata metadata = new Metadata();
    metadata.set(TikaCoreProperties.RESOURCE_NAME_KEY, fileName);
    String contentTypeOverride = getContentTypeOverride(fileName);
    if (contentTypeOverride != null) {
      metadata.set(TikaCoreProperties.CONTENT_TYPE_USER_OVERRIDE, contentTypeOverride);
    }
    try (TikaInputStream fis = TikaInputStream.get(file.toPath(), metadata)) {
      return tika.detect(fis, metadata);
    } catch (IOException e) {
      return "application/octet-stream";
    }
  }

  @Nullable
  private String getContentTypeOverride(String fileName) {
    String fileNameLowerCase = fileName.toLowerCase();
    if (fileNameLowerCase.endsWith(".png")) {
      return "image/png";
    }
    if (fileNameLowerCase.endsWith(".gif")) {
      return "image/gif";
    }
    if (fileNameLowerCase.endsWith(".jpg") || fileNameLowerCase.endsWith(".jpeg")) {
      return "image/jpeg";
    }
    if (fileNameLowerCase.endsWith(".webp")) {
      return "image/webp";
    }
    return null;
  }
}
