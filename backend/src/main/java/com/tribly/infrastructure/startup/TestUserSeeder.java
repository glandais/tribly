package com.tribly.infrastructure.startup;

import com.tribly.domain.user.User;
import com.tribly.repository.user.UserRepository;
import io.quarkus.arc.profile.IfBuildProfile;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Seeds test users on application startup in dev profile
 */
@ApplicationScoped
@IfBuildProfile("dev")
public class TestUserSeeder {

  private static final Logger LOG = Logger.getLogger(TestUserSeeder.class);

  @Inject UserRepository userRepository;

  @Transactional
  void onStart(@Observes StartupEvent event) {
    LOG.info("Seeding test users for development...");

    // Admin user
    seedUser("admin@example.com", "Admin");

    // Regular test users (user1 through user6)
    for (int i = 1; i <= 6; i++) {
      seedUser("user" + i + "@example.com", "User " + i);
    }

    LOG.info("Test users seeded successfully");
  }

  private void seedUser(String email, String displayName) {
    if (userRepository.findByEmail(email).isEmpty()) {
      User user = new User(email, displayName);
      user.markEmailVerified();
      userRepository.persist(user);
      LOG.infof("Created test user: %s", email);
    } else {
      LOG.debugf("Test user already exists: %s", email);
    }
  }
}
