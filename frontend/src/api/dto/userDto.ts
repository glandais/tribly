import type { GpsServiceConnectionDto } from './gpsServiceConnectionDto.ts'
import type { Instant } from './instant.ts'
import type { PlatformRole } from './platformRole.ts'
import type { SocialIdentityDto } from './socialIdentityDto.ts'
import type { ThemePreference } from './themePreference.ts'
import type { UnitSystem } from './unitSystem.ts'

/**
 * User profile data
 */
export interface UserDto {
  /** User ID (TSID) */
  id: string
  /** User email address */
  email: string
  /** User display name */
  displayName: string
  /** User avatar URL */
  avatarUrl?: string
  /** Account creation timestamp */
  createdAt?: Instant
  /** Preferred unit system (metric or imperial) */
  unitSystem?: UnitSystem
  /** Preferred colour scheme. Null means the user never chose one — distinct from SYSTEM, which they did choose — so a client is free to follow the device. */
  theme?: ThemePreference
  /** Preferred language as a BCP-47 tag. Null means the user never chose one; the client then follows the device or the domain. */
  language?: string
  /** Preferred IANA timezone (e.g. 'Europe/Paris'). Null means the user never chose one; the client then follows the browser. */
  timezone?: string
  /** Whether team members may reach this user through the classified-ad relay. True unless they explicitly opted out, so an account that predates the preference is contactable. */
  contactableByMembers: boolean
  /** Platform role (null if regular user) */
  platformRole?: PlatformRole
  /** Whether the account's email has been verified */
  emailVerified: boolean
  /** True when the account still needs a real, verified email (e.g. a migrated Strava account with a placeholder address) */
  requiresEmail: boolean
  /** Connected GPS services */
  connectedServices?: GpsServiceConnectionDto[]
  /** Linked external identities (e.g. Strava) */
  socialIdentities?: SocialIdentityDto[]
}
