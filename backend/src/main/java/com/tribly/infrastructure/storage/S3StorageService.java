package com.tribly.infrastructure.storage;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
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

  @ConfigProperty(name = "storage.bucket", defaultValue = "tribly")
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

  @Override
  public void store(
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
  public String getS3Path(String key) {
    return "s3://" + bucket + "/" + key;
  }
}
