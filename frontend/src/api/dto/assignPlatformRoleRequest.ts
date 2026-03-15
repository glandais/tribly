import type { PlatformRole } from './platformRole'

/**
 * Request to assign or remove platform role
 */
export interface AssignPlatformRoleRequest {
  /** Platform role to assign (null to remove) */
  role?: PlatformRole
}
