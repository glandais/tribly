import type { PlatformRole } from './platformRole.ts'

/**
 * Request to assign or remove platform role
 */
export interface AssignPlatformRoleRequest {
  /** Platform role to assign (null to remove) */
  role?: PlatformRole
}
