/**
 * Whether the current user silenced one of their teams
 */
export interface NotificationTeamPreferenceDto {
  /** Team slug */
  teamSlug: string
  /** Team name */
  teamName: string
  /** Muted: none of the team's announcements (publications) reach the user, inbox included. What concerns them personally — a cancelled ride they joined, a reply — still does. */
  muted: boolean
}
