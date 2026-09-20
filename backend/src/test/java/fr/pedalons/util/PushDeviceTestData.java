package fr.pedalons.util;

import fr.pedalons.domain.notification.PushDevice;
import fr.pedalons.domain.user.User;
import fr.pedalons.enums.PushPlatform;
import fr.pedalons.repository.notification.PushDeviceRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;

/** Reads and seeds {@code push_devices} for tests, each call in its own transaction. */
@ApplicationScoped
public class PushDeviceTestData {

  @Inject PushDeviceRepository deviceRepository;

  @Transactional
  public List<PushDevice> of(User user) {
    return deviceRepository.findByUser(user.getId());
  }

  @Transactional
  public void seed(User user, PushPlatform platform, String token) {
    deviceRepository.persist(new PushDevice(user, platform, token, Instant.now()));
  }
}
