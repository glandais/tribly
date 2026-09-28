package fr.pedalons.enums;

/**
 * Where a push device lives. All three go through FCM — iOS via APNs behind it, the web via the
 * browser's push service — so the value is not a routing decision; it is what lets an operator read
 * the table, and what {@code FcmClient} uses to pick the platform-specific block of an FCM message.
 */
public enum PushPlatform {
  ANDROID,
  IOS,
  /** A browser, through the site's service worker — usually the site installed as an app. */
  WEB
}
