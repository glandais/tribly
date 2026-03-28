package fr.pedalons.util;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MinIOContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

/**
 * Quarkus test resource that starts MinIO and imgproxy containers for storage and image processing
 * tests. Both containers share a Docker network so imgproxy can access MinIO by hostname.
 */
public class MinioTestResource implements QuarkusTestResourceLifecycleManager {

  private Network network;
  private MinIOContainer minio;
  private GenericContainer<?> imgproxy;

  @Override
  public Map<String, String> start() {
    network = Network.newNetwork();

    minio =
        new MinIOContainer("minio/minio:RELEASE.2025-01-20T14-49-07Z")
            .withUserName("minioadmin")
            .withPassword("minioadmin")
            .withNetwork(network)
            .withNetworkAliases("minio");
    minio.start();

    imgproxy =
        new GenericContainer<>("ghcr.io/imgproxy/imgproxy:v3.30.1")
            .withNetwork(network)
            .withExposedPorts(8080)
            .withEnv("IMGPROXY_USE_S3", "true")
            .withEnv("AWS_ACCESS_KEY_ID", "minioadmin")
            .withEnv("AWS_SECRET_ACCESS_KEY", "minioadmin")
            .withEnv("IMGPROXY_S3_ENDPOINT", "http://minio:9000")
            .withEnv("AWS_REGION", "us-east-1")
            .waitingFor(new HttpWaitStrategy().forPath("/health").forPort(8080));
    imgproxy.start();

    String minioEndpoint = minio.getS3URL();
    String imgproxyUrl = "http://" + imgproxy.getHost() + ":" + imgproxy.getMappedPort(8080);

    return Map.of(
        "quarkus.s3.endpoint-override", minioEndpoint,
        "quarkus.s3.aws.credentials.static-provider.access-key-id", "minioadmin",
        "quarkus.s3.aws.credentials.static-provider.secret-access-key", "minioadmin",
        "storage.bucket", "pedalons-test",
        "quarkus.rest-client.imgproxy.url", imgproxyUrl);
  }

  @Override
  public void stop() {
    if (imgproxy != null) {
      imgproxy.stop();
    }
    if (minio != null) {
      minio.stop();
    }
    if (network != null) {
      network.close();
    }
  }
}
