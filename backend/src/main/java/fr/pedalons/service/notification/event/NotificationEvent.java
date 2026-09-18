package fr.pedalons.service.notification.event;

import fr.pedalons.enums.NotificationType;

/**
 * A business event that may notify someone — the typed half of a {@code notification_events} row.
 *
 * <p>Sealed, so the dispatcher can switch over it exhaustively: adding a record here without
 * teaching {@code NotificationRecipientResolver} what to do with it is a compile error, not a
 * notification that silently goes nowhere.
 *
 * <p>Records carry identifiers only. They are serialized into the row's payload and read back by the
 * dispatcher, which reloads the entities and decides then whether the event still matters — a ride
 * published and unpublished within the same minute must not notify anyone.
 */
public sealed interface NotificationEvent
    permits RidePublished,
        RideCancelled,
        TripPublished,
        TripCancelled,
        PostPublished,
        CommentReplied {

  NotificationType type();

  /**
   * The identity of the event for deduplication. Every record of the first pass uses {@code
   * TYPE:subjectId}, so a ride is announced once however many times it flips between draft and
   * published, and cancelled once however many times it is cancelled and restored. A type that must
   * fire repeatedly for the same subject (a reminder per day, say) puts more into its key.
   */
  String dedupKey();

  /** The record class a payload of {@code type} deserializes to. */
  static Class<? extends NotificationEvent> recordClass(NotificationType type) {
    return switch (type) {
      case RIDE_PUBLISHED -> RidePublished.class;
      case RIDE_CANCELLED -> RideCancelled.class;
      case TRIP_PUBLISHED -> TripPublished.class;
      case TRIP_CANCELLED -> TripCancelled.class;
      case POST_PUBLISHED -> PostPublished.class;
      case COMMENT_REPLY -> CommentReplied.class;
    };
  }
}
