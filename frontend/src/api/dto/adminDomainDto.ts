import type { Instant } from './instant'

/**
 * Admin domain view with statistics
 */
export interface AdminDomainDto {
  /** Domain ID (TSID) */
  id: string
  /** Domain hostname */
  domain: string
  /** Domain display name */
  name: string
  /** Base URL for the domain */
  baseUrl: string
  /** Whether domain is single-team mode */
  singleTeam: boolean
  /** Android app SHA-256 certificate fingerprints for passkey origin verification (comma-separated, colon-hex format) */
  androidFingerprints?: string
  /** Whether domain is active */
  active: boolean
  /** Number of teams in this domain */
  teamCount: number
  /** Number of users in this domain */
  userCount: number
  /** Domain creation timestamp */
  createdAt: Instant
}
