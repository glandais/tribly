package fr.pedalons.enums;

/** Lifecycle of a self-service GDPR data export job. */
public enum UserExportStatus {
  /** Requested, waiting for the poller to claim it. */
  PENDING,
  /** Claimed by the poller; the ZIP is being built. */
  PROCESSING,
  /** ZIP stored and downloadable until {@code expiresAt}. */
  READY,
  /** Gave up after repeated failures; nothing to download. */
  FAILED,
  /** Past its expiry — the S3 object and the download token are gone, the row is kept for history. */
  EXPIRED
}
