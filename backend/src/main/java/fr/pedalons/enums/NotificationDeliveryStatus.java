package fr.pedalons.enums;

/** Lifecycle of one notification on one out-of-app channel. */
public enum NotificationDeliveryStatus {
  /** Waiting for {@code nextAttemptAt}. */
  PENDING,
  /** Claimed by a sender tick. */
  SENDING,
  /** Handed to the provider. */
  SENT,
  /** Not sent on purpose — the recipient was deleted, or the channel was switched off since. */
  SKIPPED,
  /** Gave up after repeated failures. */
  FAILED
}
