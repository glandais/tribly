import type { NotificationChannel } from './notificationChannel.ts'
import type { NotificationPreferenceDto } from './notificationPreferenceDto.ts'

/**
 * The current user's notification preferences
 */
export interface NotificationPreferencesDto {
  /** Channels that can be configured on this server, in display order */
  channels: NotificationChannel[]
  /** One cell per type and configurable channel */
  preferences: NotificationPreferenceDto[]
}
