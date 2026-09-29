package fr.pedalons.infrastructure.storage;

import fr.pedalons.common.exception.BadRequestException;
import fr.pedalons.dto.error.ErrorCode;
import fr.pedalons.infrastructure.image.ImageFormat;
import fr.pedalons.infrastructure.image.ImageMetadataStripper;
import fr.pedalons.infrastructure.image.MalformedImageException;
import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/** S3-based implementation of {@link StorageService} for MinIO/S3 storage. */
@Startup
@ApplicationScoped
public class S3StorageService implements StorageService {

  private static final Logger LOG = Logger.getLogger(S3StorageService.class);

  @Inject S3Client s3Client;

  @ConfigProperty(name = "storage.bucket", defaultValue = "pedalons")
  String bucket;

  @PostConstruct
  void init() {
    ensureBucketExists();
  }

  private void ensureBucketExists() {
    try {
      s3Client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
      LOG.infof("S3 bucket '%s' exists", bucket);
    } catch (NoSuchBucketException e) {
      LOG.infof("Creating S3 bucket '%s'", bucket);
      s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
    }
  }

  @Override
  public void store(String key, InputStream content, String contentType, long contentLength) {
    store(key, content, contentType, contentLength, Map.of());
  }

  /**
   * Stores {@code content}, without its metadata when it is an image.
   *
   * <p>The one place every file reaches the bucket through — uploaded assets and attachments,
   * avatars, generated thumbnails, the biketeam import — hence the one place metadata is removed,
   * so that no new write path can forget to (docs/LEDGER_*.md API-43). The format is read from the
   * bytes, not from {@code contentType}: a JPEG uploaded as an attachment under a generic type is
   * cleaned all the same.
   *
   * @throws BadRequestException {@link ErrorCode#FILE_TYPE_REJECTED} for an image whose metadata
   *     cannot be removed (TIFF, HEIF…), {@link ErrorCode#INVALID_FORMAT} for an image too broken
   *     to walk; nothing is stored in either case
   */
  @Override
  public void store(
      String key,
      InputStream content,
      String contentType,
      long contentLength,
      Map<String, String> metadata) {
    try {
      BufferedInputStream in = new BufferedInputStream(content, 8192);
      in.mark(ImageFormat.SNIFF_LENGTH);
      ImageFormat format = ImageFormat.sniff(in.readNBytes(ImageFormat.SNIFF_LENGTH));
      in.reset();
      if (format.isRefused()) {
        LOG.warnf("Refusing to store %s: %s image, its metadata cannot be removed", key, format);
        throw new BadRequestException(ErrorCode.FILE_TYPE_REJECTED);
      }
      if (!format.isStrippable()) {
        put(key, in, contentType, contentLength, metadata);
        return;
      }
      Path stripped = Files.createTempFile("pedalons-stripped-", ".tmp");
      try {
        try (OutputStream out = Files.newOutputStream(stripped)) {
          ImageMetadataStripper.strip(format, in, out);
        } catch (MalformedImageException e) {
          LOG.warnf("Refusing to store %s: malformed %s (%s)", key, format, e.getMessage());
          throw new BadRequestException(ErrorCode.INVALID_FORMAT, e);
        }
        long size = Files.size(stripped);
        LOG.debugf(
            "Stripped metadata of %s (%s): %d -> %d bytes", key, format, contentLength, size);
        try (InputStream strippedContent = Files.newInputStream(stripped)) {
          put(key, strippedContent, contentType, size, metadata);
        }
      } finally {
        Files.deleteIfExists(stripped);
      }
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to store " + key, e);
    }
  }

  private void put(
      String key,
      InputStream content,
      String contentType,
      long contentLength,
      Map<String, String> metadata) {
    PutObjectRequest.Builder requestBuilder =
        PutObjectRequest.builder().bucket(bucket).key(key).contentType(contentType);

    if (metadata != null && !metadata.isEmpty()) {
      requestBuilder.metadata(metadata);
    }

    s3Client.putObject(requestBuilder.build(), RequestBody.fromInputStream(content, contentLength));
    LOG.debugf("Stored object: s3://%s/%s with metadata: %s", bucket, key, metadata);
  }

  @Override
  public InputStream retrieve(String key) {
    GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();

    return s3Client.getObject(request);
  }

  @Override
  public byte[] retrieveHead(String key, int length) {
    GetObjectRequest request =
        GetObjectRequest.builder().bucket(bucket).key(key).range("bytes=0-" + (length - 1)).build();
    try (InputStream in = s3Client.getObject(request)) {
      return in.readNBytes(length);
    } catch (IOException e) {
      throw new UncheckedIOException("Failed to read the head of " + key, e);
    }
  }

  @Override
  public void delete(String key) {
    DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(bucket).key(key).build();

    s3Client.deleteObject(request);
    LOG.debugf("Deleted object: s3://%s/%s", bucket, key);
  }

  @Override
  public boolean exists(String key) {
    try {
      HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();

      s3Client.headObject(request);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    }
  }

  @Override
  public long size(String key) {
    try {
      HeadObjectRequest request = HeadObjectRequest.builder().bucket(bucket).key(key).build();
      return s3Client.headObject(request).contentLength();
    } catch (NoSuchKeyException e) {
      return -1;
    }
  }

  @Override
  public String getS3Path(String key) {
    return "s3://" + bucket + "/" + key;
  }
}
