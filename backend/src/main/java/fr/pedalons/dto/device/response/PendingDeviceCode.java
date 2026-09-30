package fr.pedalons.dto.device.response;

import java.time.Instant;

/**
 * What the confirmation screen shows of a pending pairing code (docs/LEDGER_*.md SEC-2): the api
 * layer reads this, never the {@code DeviceCode} entity.
 *
 * @param clientId which kind of device asks (e.g. {@code karoo}, {@code garmin})
 * @param requestedAt when the device asked for the code
 */
public record PendingDeviceCode(boolean authorized, String clientId, Instant requestedAt) {}
