import type { Instant } from './instant'
import type { PlatformRole } from './platformRole'

/**
 * Admin user view with domain info
 */
export interface AdminUserDto {
  /** User ID (TSID) */
  id: string
  /** User email address */
  email: string
  /** User display name */
  displayName: string
  /** Domain ID this user belongs to */
  domainId: string
  /** Domain hostname */
  domainName: string
  /** Platform role (null if regular user) */
  platformRole?: PlatformRole
  /** Whether email is verified */
  emailVerified: boolean
  /** User creation timestamp */
  createdAt: Instant
  /** Last login timestamp */
  lastLoginAt?: Instant
}
