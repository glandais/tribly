import type { NotificationChannel } from './notificationChannel.ts'
import type { NotificationType } from './notificationType.ts'

/**
 * Switch one notification type on or off on one channel
 */
export interface NotificationPreferenceUpdate {
  /** Notification type */
  type: NotificationType
  /** Delivery channel. IN_APP is refused: the inbox is always on. */
  channel: NotificationChannel
  /** Whether to deliver it */
  enabled: boolean
}
