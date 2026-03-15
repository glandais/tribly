import type { UnitSystem } from './unitSystem'

/**
 * User profile update request
 */
export interface UpdateUserRequest {
  /**
   * User display name
   * @minLength 1
   * @maxLength 200
   */
  displayName?: string
  /** Preferred unit system */
  unitSystem?: UnitSystem
}
