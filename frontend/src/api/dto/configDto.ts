/**
 * Application configuration
 */
export interface ConfigDto {
  /** WebAuthn Relying Party ID (domain) */
  webAuthnRpId: string
  /** Application name */
  appName: string
  /** Single team mode - team creation disabled */
  singleTeam: boolean
}
