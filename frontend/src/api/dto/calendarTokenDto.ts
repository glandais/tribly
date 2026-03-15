/**
 * Calendar token information
 */
export interface CalendarTokenDto {
  /** ICS feed token (include in URL) */
  token: string
  /** Global ICS feed URL */
  globalFeedUrl: string
  /** Team-specific feed URL template */
  teamFeedUrlTemplate: string
}
