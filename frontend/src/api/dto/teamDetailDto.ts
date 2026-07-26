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
  /** Plain-text opening of the about page, flattened and cut on a word boundary at about 200 characters. Null when the about page holds no text. Lets a team card render its two lines without parsing the markdown client-side. */
  excerpt?: string
  /** URL template of the team's logo, when it has one. Same picture as about.assets.logo, hoisted so a card does not have to walk the asset inventory to find it. */
  logoUrl?: string
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
  /** Rides of this team dated in the future that the caller may open. Follows the same visibility rules as the ride listing, so it never announces more than the caller can actually see. */
  upcomingRideCount: number
  /** Routes of this team the caller may open, under the same visibility rules as the route listing. */
  routeCount: number
  /** Current user's role (null if not a member) */
  role?: TeamRole
  /** Team creation timestamp */
  createdAt: Instant
  /** Team location coordinates [longitude, latitude] */
  geometry?: TeamDetailDtoGeometry
}
