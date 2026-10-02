package fr.pedalons.enums;

import org.jspecify.annotations.Nullable;

/** The kind of device a pairing (RFC 8628) was made from, read from its {@code clientId}. */
public enum PairedDeviceType {
  KAROO,
  GARMIN,
  /** A client id neither app sends: the device flow accepts any, {@code device} by default. */
  OTHER;

  public static PairedDeviceType fromClientId(@Nullable String clientId) {
    if ("karoo".equals(clientId)) {
      return KAROO;
    }
    if ("garmin".equals(clientId)) {
      return GARMIN;
    }
    return OTHER;
  }
}
