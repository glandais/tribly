import type { NotificationPreferenceUpdate } from './notificationPreferenceUpdate.ts'

/**
 * Notification preference cells to change
 */
export interface NotificationPreferencesRequest {
  /**
   * The cells to change
   * @maxItems 200
   */
  preferences: NotificationPreferenceUpdate[]
}
