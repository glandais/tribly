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
        CommentReplied,
        RideReminder,
        RideUpdated,
        RideJoined,
        CommentOnPublication,
        TeamInvited,
        ContentReported {

  NotificationType type();

  /**
   * The identity of the event for deduplication. Most records use {@code TYPE:subjectId}, so a ride is announced once however many times it flips between draft and
   * published, and cancelled once however many times it is cancelled and restored. A type that must
   * fire repeatedly for the same subject (a reminder per day, say) puts more into its key.
   */
  String dedupKey();

  /**
   * Whether the dedup key is only held while the event waits to be fanned out. False for almost
   * every type: a ride is announced once, ever. True for a change that can happen again — a ride
   * moved twice in a month is two notifications — but whose edits in quick succession should make
   * one: a second edit while the first still waits finds the key taken and is folded into it.
   */
  default boolean coalescesWhilePending() {
    return false;
  }

  /** The record class a payload of {@code type} deserializes to. */
  static Class<? extends NotificationEvent> recordClass(NotificationType type) {
    return switch (type) {
      case RIDE_PUBLISHED -> RidePublished.class;
      case RIDE_CANCELLED -> RideCancelled.class;
      case TRIP_PUBLISHED -> TripPublished.class;
      case TRIP_CANCELLED -> TripCancelled.class;
      case POST_PUBLISHED -> PostPublished.class;
      case COMMENT_REPLY -> CommentReplied.class;
      case RIDE_REMINDER -> RideReminder.class;
      case RIDE_UPDATED -> RideUpdated.class;
      case RIDE_JOINED -> RideJoined.class;
      case COMMENT_ON_MY_PUBLICATION -> CommentOnPublication.class;
      case TEAM_INVITATION -> TeamInvited.class;
      case CONTENT_REPORTED -> ContentReported.class;
    };
  }
}
