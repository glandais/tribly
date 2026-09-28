package fr.pedalons.service.device;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Plain unit test: no Quarkus, the verification path depends on the client id alone. */
class DeviceAuthServiceTest {

  @Test
  void garmin_isSentToTheGarminPage() {
    assertEquals("/garmin", DeviceAuthService.verificationPath("garmin"));
  }

  @Test
  void karoo_isSentToTheKarooPage() {
    assertEquals("/karoo", DeviceAuthService.verificationPath("karoo"));
  }

  @Test
  void anyOtherClient_keepsTheKarooPage() {
    assertEquals("/karoo", DeviceAuthService.verificationPath("device"));
  }
}
