import type { Instant } from './instant.ts'

/**
 * A beta program sign-up, as seen by an admin
 */
export interface BetaSignupDto {
  /** Sign-up ID */
  id: string
  /** Email */
  email: string
  /** When the sign-up was submitted */
  createdAt: Instant
}
