package com.tribly.infrastructure.dev;

import com.tribly.repository.user.UserRepository;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@ApplicationScoped
@IfBuildProfile("dev")
public class DevDataStartupObserver {

  private static final Logger LOG = Logger.getLogger(DevDataStartupObserver.class);

  @ConfigProperty(name = "tribly.generate-data", defaultValue = "false")
  boolean generateData;

  @Inject DevDataGenerator devDataGenerator;

  @Inject UserRepository userRepository;

  void onStart(@Observes StartupEvent event) {
    if (!generateData) {
      LOG.debug("Dev data generation disabled (use -Dtribly.generate-data=true to enable)");
      return;
    }

    if (!isDatabaseEmpty()) {
      LOG.info("Database already contains data, skipping dev data generation");
      return;
    }

    LOG.info("Starting dev data generation...");
    devDataGenerator.generate();
    LOG.infof("Dev data generation completed");
  }

  private boolean isDatabaseEmpty() {
    long count = userRepository.count();
    return count == 0;
  }
}
