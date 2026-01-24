package com.tribly.util;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.MinIOContainer;

/**
 * Quarkus test resource that starts a MinIO container for S3 storage tests. This resource starts a
 * MinIO container and injects the S3 configuration properties.
 *
 * <p>Usage: Add @WithTestResource(MinioTestResource.class) to your test class, or add it globally
 * in src/main/resources/application.properties with %test.quarkus.test.resource-classes.
 */
public class MinioTestResource implements QuarkusTestResourceLifecycleManager {

  private MinIOContainer minio;

  @Override
  public Map<String, String> start() {
    minio =
        new MinIOContainer("minio/minio:RELEASE.2025-01-20T14-49-07Z")
            .withUserName("minioadmin")
            .withPassword("minioadmin");
    minio.start();

    String endpoint = minio.getS3URL();

    return Map.of(
        "quarkus.s3.endpoint-override", endpoint,
        "quarkus.s3.aws.credentials.static-provider.access-key-id", "minioadmin",
        "quarkus.s3.aws.credentials.static-provider.secret-access-key", "minioadmin",
        "storage.bucket", "tribly-test");
  }

  @Override
  public void stop() {
    if (minio != null) {
      minio.stop();
    }
  }
}
