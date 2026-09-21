import type { NotificationPreferenceUpdate } from './notificationPreferenceUpdate.ts'
import type { NotificationTeamPreferenceUpdate } from './notificationTeamPreferenceUpdate.ts'

/**
 * Notification preference cells to change
 */
export interface NotificationPreferencesRequest {
  /**
   * The cells to change
   * @maxItems 200
   */
  preferences: NotificationPreferenceUpdate[]
  /**
   * The teams to mute or unmute
   * @maxItems 200
   */
  teams?: NotificationTeamPreferenceUpdate[]
  /** Switch the daily e-mail digest on or off; absent leaves it */
  emailDigest?: boolean
}
