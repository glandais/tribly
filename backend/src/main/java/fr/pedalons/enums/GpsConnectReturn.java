package fr.pedalons.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Where the GPS OAuth callback sends the browser back to. An enumeration and never a URL: the
 * callback is public, and a free return address would be an open redirect (docs/LEDGER_*.md
 * SEC-12). The path is the same in every locale.
 */
@Getter
@RequiredArgsConstructor
public enum GpsConnectReturn {
  /**
   * The profile's « Appareils et services » page, where the services are connected — the default.
   * The profile's sub-page since the profile redesign, not the overview (contracts/routes.yaml,
   * profileDevices).
   */
  PROFILE("/profile/devices"),
  /** The Karoo pairing page, which then tells the rider the Karoo is ready (ledger API-63). */
  DEVICE_KAROO("/karoo");

  private final String path;
}
