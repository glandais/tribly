import type { NotificationChannel } from './notificationChannel.ts'
import type { NotificationType } from './notificationType.ts'

/**
 * One cell of the notification preferences: a type on a channel
 */
export interface NotificationPreferenceDto {
  /** Notification type */
  type: NotificationType
  /** Delivery channel */
  channel: NotificationChannel
  /** Whether it is delivered, as currently in effect */
  enabled: boolean
  /** What applies when the user never touched this cell. Lets a client offer a 'restore defaults' without hard-coding them. */
  enabledByDefault: boolean
}
