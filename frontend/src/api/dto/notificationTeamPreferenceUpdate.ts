/**
 * Mute or unmute one of the current user's teams
 */
export interface NotificationTeamPreferenceUpdate {
  /**
   * Team slug — a team the user belongs to
   * @pattern \S
   */
  teamSlug: string
  /** Whether to mute its announcements */
  muted: boolean
}
