package fr.pedalons.util;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import java.nio.file.Path;
import java.util.Map;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.utility.MountableFile;

/**
 * Quarkus test resource that starts a TileServer GL container for map tile tests. Mounts the
 * tileserver config and styles from the project data directory.
 */
public class TileserverTestResource implements QuarkusTestResourceLifecycleManager {

  private GenericContainer<?> tileserver;

  @Override
  public Map<String, String> start() {
    Path dataDir = Path.of("../data/tileserver").toAbsolutePath().normalize();

    tileserver =
        new GenericContainer<>(ContainerImages.compose("tileserver"))
            .withExposedPorts(8080)
            .withCopyFileToContainer(MountableFile.forHostPath(dataDir), "/data")
            .waitingFor(new HttpWaitStrategy().forPath("/health").forPort(8080));
    tileserver.start();

    String tileserverUrl = "http://" + tileserver.getHost() + ":" + tileserver.getMappedPort(8080);

    return Map.of("tileserver.url", tileserverUrl);
  }

  @Override
  public void stop() {
    if (tileserver != null) {
      tileserver.stop();
    }
  }
}
