import type { GpsServiceConnectionDto } from './gpsServiceConnectionDto.ts'
import type { Instant } from './instant.ts'
import type { PlatformRole } from './platformRole.ts'
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
  /** Platform role (null if regular user) */
  platformRole?: PlatformRole
  /** Connected GPS services */
  connectedServices?: GpsServiceConnectionDto[]
}
