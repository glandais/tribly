package fr.pedalons.enums;

/** Lifecycle of a {@code notification_events} row — the outbox. */
public enum NotificationEventStatus {
  /** Written by the business transaction, waiting for the dispatcher. */
  PENDING,
  /** Claimed by the dispatcher; recipients are being resolved. */
  PROCESSING,
  /** Fanned out: its notifications and deliveries exist. */
  DONE,
  /** No longer relevant when processed (unpublished, deleted, in the past) — nobody notified. */
  SKIPPED,
  /** Gave up after repeated failures. */
  FAILED
}
