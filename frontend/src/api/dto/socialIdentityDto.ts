import type { Instant } from './instant.ts'
import type { SocialProvider } from './socialProvider.ts'

/**
 * A linked external identity (e.g. Strava)
 */
export interface SocialIdentityDto {
  /** Provider identifier */
  provider: SocialProvider
  /** Display name of the provider */
  displayName: string
  /** When the identity was linked */
  linkedAt: Instant
}
