import type { RideTemplateGroupRequest } from './rideTemplateGroupRequest.ts'
import type { Status } from './status.ts'
import type { Visibility } from './visibility.ts'

/**
 * Ride template request
 */
export interface RideTemplateRequest {
  /**
   * Template name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Template description (markdown) */
  markdown: string
  /** Visibility level */
  visibility: Visibility
  /** Default status for rides created from this template */
  status: Status
  /** Template groups */
  groups: RideTemplateGroupRequest[]
}
