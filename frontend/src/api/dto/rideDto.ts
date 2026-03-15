import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { PlaceDetailDto } from './placeDetailDto'
import type { PublicUserDto } from './publicUserDto'
import type { RideDtoType } from './rideDtoType'
import type { RideGroupDto } from './rideGroupDto'
import type { Status } from './status'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { Visibility } from './visibility'

/**
 * Ride summary data
 */
export interface RideDto {
  type: RideDtoType
  /** Team */
  team: TeamPublicationDto
  /** Publication ID (TSID) */
  id: string
  /** Publication URL slug */
  slug: string
  /** Publication name */
  name: string
  /** Publication media */
  media: MediaDto
  /** Publication date/time */
  dateTime: Instant
  /** Publication status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Publication timestamp */
  publishAt?: Instant
  /** Creation timestamp */
  createdAt?: Instant
  /** Route slug */
  routeSlug?: string
  /** Number of participants */
  participantCount: number
  /** Number of groups */
  groupCount: number
  /** Ride groups */
  groups: RideGroupDto[]
  /** Start place */
  startPlace?: PlaceDetailDto
  /** End place */
  endPlace?: PlaceDetailDto
  /** Preview of first participants (max 5) */
  topParticipants: PublicUserDto[]
  /** Thumbnail URL (light) */
  thumbnailLightUrl?: string
  /** Thumbnail URL (dark) */
  thumbnailDarkUrl?: string
}
