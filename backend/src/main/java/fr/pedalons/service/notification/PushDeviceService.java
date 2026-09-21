package fr.pedalons.service.notification;

import fr.pedalons.dto.notifications.request.PushDeviceRegistration;
import fr.pedalons.repository.notification.PushDeviceRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.time.Instant;

/**
 * The address book of the push channel, from the app's side.
 *
 * <p>No domain filter here, unlike the inbox: a token belongs to an install, and the same person
 * signed in on two domains is the same phone. What is scoped is the user — a member can only
 * register or drop their own devices.
 */
@ApplicationScoped
public class PushDeviceService {

  @Inject PushDeviceRepository deviceRepository;
  @Inject PedalonsQueryContext pedalonsContext;

  /**
   * Registers, or refreshes, one device. Idempotent by design: the app calls it at every launch and
   * whenever FCM rotates the token.
   *
   * <p>A token already known is <em>moved</em> to the caller rather than duplicated. That is what
   * makes signing in on a borrowed phone stop notifying its previous owner.
   */
  @Logged
  @Transactional
  public void register(PushDeviceRegistration registration) {
    deviceRepository.upsert(
        pedalonsContext.getUserId(),
        registration.platform(),
        registration.token(),
        registration.deviceName(),
        registration.appVersion(),
        Instant.now());
  }

  /**
   * Drops one device. Idempotent, and silent about tokens that are not the caller's: signing out
   * must not become a way to probe whether a token exists.
   */
  @Logged
  @Transactional
  public void unregister(String token) {
    deviceRepository.deleteByUserAndToken(pedalonsContext.getUserId(), token);
  }
}
