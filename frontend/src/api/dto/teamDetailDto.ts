import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { TeamDetailDtoGeometry } from './teamDetailDtoGeometry'
import type { TeamPageSummaryDto } from './teamPageSummaryDto'
import type { TeamRole } from './teamRole'
import type { Visibility } from './visibility'

/**
 * Detailed team information
 */
export interface TeamDetailDto {
  /** Team ID (TSID) */
  id: string
  /** Team name */
  name: string
  /** Team URL slug */
  slug: string
  /** About page content */
  about: MediaDto
  /** Additional team pages */
  pages?: TeamPageSummaryDto[]
  /** Whether the team is public */
  visibility: Visibility
  /** Trips enabled */
  enableTrips: boolean
  /** Ads enabled */
  enableAds: boolean
  /** Number of team members */
  memberCount: number
  /** Current user's role (null if not a member) */
  role?: TeamRole
  /** Team creation timestamp */
  createdAt: Instant
  /** Team location coordinates [longitude, latitude] */
  geometry?: TeamDetailDtoGeometry
}
