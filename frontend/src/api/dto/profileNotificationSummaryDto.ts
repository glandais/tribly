import type { NotificationChannel } from './notificationChannel.ts'

/**
 * Where the current user's notifications go, summed up
 */
export interface ProfileNotificationSummaryDto {
  /** Channels that can be configured on this server, in display order */
  channels: NotificationChannel[]
  /** Among channels, those on which at least one notification type is turned on for the user (their choices, or the defaults they never changed) */
  enabledChannels: NotificationChannel[]
  /** Whether non-urgent e-mails are held for a daily digest. Always false when EMAIL is not among channels. */
  emailDigest: boolean
}
