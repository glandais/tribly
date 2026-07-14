import type { Instant } from './instant.ts'
import type { PublicationType } from './publicationType.ts'

/**
 * A ride or trip that uses a route
 */
export interface RouteUsageDto {
  /** Publication type (RIDE or TRIP) */
  type: PublicationType
  /** Publication URL slug */
  slug: string
  /** Publication name */
  name: string
  /** Publication date/time */
  dateTime: Instant
  /** Slug of the team owning the publication */
  teamSlug: string
  /** Whether the publication references the route directly (not only via a child) */
  referencedDirectly: boolean
  /** Names of the ride groups or trip stages that reference the route, if any */
  viaChildNames: string[]
}
