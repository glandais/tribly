package fr.pedalons.enums;

/**
 * The store a push device came from. Both go through FCM — iOS via APNs behind it — so the value is
 * not a routing decision; it is what lets an operator read the table, and what
 * {@code PushNotificationSender} uses to pick the platform-specific block of an FCM message.
 */
public enum PushPlatform {
  ANDROID,
  IOS
}
