import type { NotificationChannel } from './notificationChannel.ts'
import type { NotificationPreferenceDto } from './notificationPreferenceDto.ts'
import type { NotificationTeamPreferenceDto } from './notificationTeamPreferenceDto.ts'

/**
 * The current user's notification preferences
 */
export interface NotificationPreferencesDto {
  /** Channels that can be configured on this server, in display order */
  channels: NotificationChannel[]
  /** One cell per type and configurable channel */
  preferences: NotificationPreferenceDto[]
  /** The user's teams on this site, each with its mute switch. Offered whatever the channels: muting also keeps the team's announcements out of the inbox. */
  teams: NotificationTeamPreferenceDto[]
  /** Non-urgent e-mails are held and sent as one digest a day, at 7:00 in the user's time zone. Cancellations, changes and reminders still leave at once. Only meaningful when EMAIL is among the channels. */
  emailDigest: boolean
}
