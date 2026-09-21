/**
 * Outcome of a test message sent to the team's webhook
 */
export interface TeamWebhookTestDto {
  /** Whether the endpoint accepted it (2xx) */
  success: boolean
  /** HTTP status the endpoint answered, if it answered */
  statusCode?: number
  /** Why it failed, when it did */
  error?: string
}
