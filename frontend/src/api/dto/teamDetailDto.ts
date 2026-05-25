import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { TeamDetailDtoGeometry } from './teamDetailDtoGeometry.ts'
import type { TeamPageSummaryDto } from './teamPageSummaryDto.ts'
import type { TeamRole } from './teamRole.ts'
import type { Visibility } from './visibility.ts'

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
  /** Posts enabled */
  enablePosts: boolean
  /** Rides enabled */
  enableRides: boolean
  /** Routes enabled */
  enableRoutes: boolean
  /** Whether visibility is editable by team admins */
  visibilityEditable: boolean
  /** Whether any domain user can join this team */
  joinable: boolean
  /** Whether team admins can add members */
  addMemberAllowed: boolean
  /** Number of team members */
  memberCount: number
  /** Current user's role (null if not a member) */
  role?: TeamRole
  /** Team creation timestamp */
  createdAt: Instant
  /** Team location coordinates [longitude, latitude] */
  geometry?: TeamDetailDtoGeometry
}
