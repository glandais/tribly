package fr.pedalons.service.config;

import fr.pedalons.dto.config.VersionDto;
import fr.pedalons.service.security.annotation.Public;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

/**
 * Exposes which build of the server is running: the API contract version (a config property bumped
 * by hand along with the contract) plus the git commit and build time that
 * git-commit-id-maven-plugin writes into {@code git.properties} at package time.
 *
 * <p>The git data is optional — the plugin is configured not to fail when there is no {@code .git}
 * (source tarball, broken shallow clone) — so both fields degrade to null instead of failing.
 */
@ApplicationScoped
public class VersionService {

  private static final Logger LOG = Logger.getLogger(VersionService.class);

  private static final String GIT_PROPERTIES = "git.properties";

  private static final Properties GIT = loadGitProperties();

  @ConfigProperty(name = "pedalons.api.version")
  String apiVersion;

  @Public
  public VersionDto getVersion() {
    return new VersionDto(
        apiVersion, GIT.getProperty("git.commit.id.abbrev"), GIT.getProperty("git.build.time"));
  }

  private static Properties loadGitProperties() {
    Properties properties = new Properties();
    try (InputStream in =
        Thread.currentThread().getContextClassLoader().getResourceAsStream(GIT_PROPERTIES)) {
      if (in == null) {
        LOG.debugf("%s not on the classpath, build info reported as null", GIT_PROPERTIES);
        return properties;
      }
      properties.load(in);
    } catch (IOException e) {
      LOG.warnf(e, "Could not read %s, build info reported as null", GIT_PROPERTIES);
    }
    return properties;
  }
}
