package fr.pedalons.service.device;

import fr.pedalons.common.exception.NotFoundException;
import fr.pedalons.dto.users.response.PairedDeviceDto;
import fr.pedalons.repository.auth.AuthSessionRepository;
import fr.pedalons.service.security.PedalonsQueryContext;
import fr.pedalons.service.security.annotation.Logged;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;

/**
 * The devices paired with the current account (docs/LEDGER_*.md API-64): each one is the session
 * its pairing opened. Unpairing revokes that session: the device's next refresh fails, and its
 * access token, which names the session, is refused at once by {@link DeviceSessionFilter}
 * (docs/LEDGER_*.md API-65).
 */
@ApplicationScoped
public class PairedDeviceService {

  @Inject AuthSessionRepository authSessionRepository;

  @Inject PedalonsQueryContext pedalonsContext;

  @Logged
  public List<PairedDeviceDto> listPairedDevices() {
    return authSessionRepository.findActiveDevicesByUserId(pedalonsContext.getUserId()).stream()
        .map(PairedDeviceDto::from)
        .toList();
  }

  /**
   * A session of someone else, of the site or the app, or already revoked answers the same 404: the
   * caller learns nothing about sessions that are not its own devices.
   */
  @Transactional
  @Logged
  public void unpairDevice(Long sessionId) {
    if (authSessionRepository.revokeDevice(sessionId, pedalonsContext.getUserId()) == 0) {
      throw new NotFoundException();
    }
  }
}
