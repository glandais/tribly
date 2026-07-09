import type { Instant } from './instant.ts'

/**
 * Admin view of a domain alias (dedicated hostname pinned to a team)
 */
export interface AdminDomainAliasDto {
  /** Alias ID (TSID) */
  id: string
  /** Alias hostname */
  hostname: string
  /** Parent domain ID (TSID) */
  domainId: string
  /** Pinned team ID (TSID) */
  pinnedTeamId: string
  /** Pinned team slug */
  pinnedTeamSlug: string
  /** Alias display name */
  name: string
  /** Base URL for the alias */
  baseUrl: string
  /** Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format) */
  androidFingerprints?: string
  /** Whether alias is active */
  active: boolean
  /** Alias creation timestamp */
  createdAt: Instant
}
