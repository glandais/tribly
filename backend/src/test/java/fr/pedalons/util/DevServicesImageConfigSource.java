package fr.pedalons.util;

import java.util.Map;
import java.util.Set;
import org.eclipse.microprofile.config.spi.ConfigSource;

/**
 * Test-only config source that overrides the Quarkus Dev Services PostgreSQL image with the tag
 * declared for the {@code postgres} service in {@code docker-compose.yml}, so the test database
 * image tracks the Dependabot-managed docker-compose entry instead of the hardcoded value in
 * application.properties.
 *
 * <p>Uses a high ordinal so it wins over application.properties (250). It lives only on the test
 * classpath, so dev and prod are unaffected — Dev Services are disabled there anyway (both profiles
 * set a JDBC URL).
 */
public class DevServicesImageConfigSource implements ConfigSource {

  private static final String KEY = "quarkus.datasource.devservices.image-name";
  private final String image = ContainerImages.compose("postgres");

  @Override
  public Map<String, String> getProperties() {
    return Map.of(KEY, image);
  }

  @Override
  public Set<String> getPropertyNames() {
    return Set.of(KEY);
  }

  @Override
  public String getValue(String key) {
    return KEY.equals(key) ? image : null;
  }

  @Override
  public String getName() {
    return "docker-compose-images";
  }

  @Override
  public int getOrdinal() {
    return 500;
  }
}
