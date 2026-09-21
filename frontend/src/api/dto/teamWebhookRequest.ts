/**
 * Create or change a team's webhook
 */
export interface TeamWebhookRequest {
  /**
   * The https URL to post to. Omit it to keep the one already set — the API never returns it in full. Required when the team has no webhook yet.
   * @maxLength 1000
   */
  url?: string
  /**
   * Language the messages are written in
   * @pattern fr|en
   */
  language: string
  /** Whether announcements are posted */
  enabled: boolean
}
