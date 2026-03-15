import type { Instant } from './instant'
import type { RideTemplateGroupDto } from './rideTemplateGroupDto'
import type { Status } from './status'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { Visibility } from './visibility'

/**
 * Ride template response
 */
export interface RideTemplateDto {
  /** Team */
  team: TeamPublicationDto
  /** Template ID (TSID) */
  id: string
  /** Template slug */
  slug: string
  /** Template name */
  name: string
  /** Template description (markdown) */
  markdown: string
  /** Visibility level */
  visibility: Visibility
  /** Default status */
  status: Status
  /** Creation timestamp */
  createdAt: Instant
  /** Last update timestamp */
  updatedAt: Instant
  /** Number of groups */
  groupCount: number
  /** Template groups */
  groups: RideTemplateGroupDto[]
}
