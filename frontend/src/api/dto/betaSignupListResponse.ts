import type { BetaSignupDto } from './betaSignupDto.ts'

/**
 * Paginated beta sign-up list response
 */
export interface BetaSignupListResponse {
  /** List of sign-ups */
  signups: BetaSignupDto[]
  /** Total number of sign-ups */
  total: number
  /** Current page number */
  page: number
  /** Page size */
  size: number
}
