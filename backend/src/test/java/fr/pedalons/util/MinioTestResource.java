package fr.pedalons.util;

import com.adobe.testing.s3mock.testcontainers.S3MockContainer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;

/**
 * Quarkus test resource that starts S3Mock and imgproxy containers for storage and image processing
 * tests. Both containers share a Docker network so imgproxy can access S3Mock by hostname.
 */
public class MinioTestResource implements QuarkusTestResourceLifecycleManager {

  private Network network;
  private S3MockContainer s3mock;
  private GenericContainer<?> imgproxy;

  @Override
  public Map<String, String> start() {
    network = Network.newNetwork();

    s3mock =
        new S3MockContainer("5.0.0")
            .withInitialBuckets("pedalons-test")
            .withNetwork(network)
            .withNetworkAliases("s3mock");
    s3mock.start();

    imgproxy =
        new GenericContainer<>("ghcr.io/imgproxy/imgproxy:v3.30.1")
            .withNetwork(network)
            .withExposedPorts(8080)
            .withEnv("IMGPROXY_USE_S3", "true")
            .withEnv("AWS_ACCESS_KEY_ID", "foo")
            .withEnv("AWS_SECRET_ACCESS_KEY", "bar")
            .withEnv("IMGPROXY_S3_ENDPOINT", "http://s3mock:9090")
            .withEnv("AWS_REGION", "us-east-1")
            .waitingFor(new HttpWaitStrategy().forPath("/health").forPort(8080));
    imgproxy.start();

    String s3Endpoint = s3mock.getHttpEndpoint();
    String imgproxyUrl = "http://" + imgproxy.getHost() + ":" + imgproxy.getMappedPort(8080);

    return Map.of(
        "quarkus.s3.endpoint-override", s3Endpoint,
        "quarkus.s3.aws.credentials.static-provider.access-key-id", "foo",
        "quarkus.s3.aws.credentials.static-provider.secret-access-key", "bar",
        "storage.bucket", "pedalons-test",
        "quarkus.rest-client.imgproxy.url", imgproxyUrl);
  }

  @Override
  public void stop() {
    if (imgproxy != null) {
      imgproxy.stop();
    }
    if (s3mock != null) {
      s3mock.stop();
    }
    if (network != null) {
      network.close();
    }
  }
}
