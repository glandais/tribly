import type { Instant } from './instant.ts'
import type { RideTemplateGroupDto } from './rideTemplateGroupDto.ts'
import type { Status } from './status.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

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
