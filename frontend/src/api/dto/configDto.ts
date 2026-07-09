/**
 * Application configuration
 */
export interface ConfigDto {
  /** WebAuthn Relying Party ID (effective host) */
  webAuthnRpId: string
  /** Application name */
  appName: string
  /** Single team mode - team creation disabled */
  singleTeam: boolean
  /** Slug of the team the site is pinned to (dedicated hostname / alias). Null on a regular multi-team domain. When set, the app roots on this team. */
  pinnedTeamSlug?: string
}
