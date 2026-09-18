package fr.pedalons.enums;

/**
 * Where a notification is delivered.
 *
 * <p>{@link #IN_APP} is not a delivery like the others: the {@code notifications} row <em>is</em>
 * the inbox entry, so it has no {@code notification_deliveries} row and no preference — every
 * notification lands in the inbox. Preferences only govern the channels that interrupt.
 */
public enum NotificationChannel {
  /** The inbox shown by the web site and the mobile app. Always on. */
  IN_APP,
  /** An e-mail through {@code EmailService}, using the generic {@code notification} template. */
  EMAIL,
  /** A mobile push (FCM, APNs through FCM). Not implemented yet: no delivery is ever created. */
  PUSH;

  /** Whether a member may switch this channel off. */
  public boolean isConfigurable() {
    return this != IN_APP;
  }
}
