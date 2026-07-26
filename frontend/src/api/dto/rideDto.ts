import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PlaceDetailDto } from './placeDetailDto.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { RideDtoType } from './rideDtoType.ts'
import type { RideGroupDto } from './rideGroupDto.ts'
import type { Status } from './status.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

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
  /** Plain-text opening of the markdown body, flattened (links become their label) and cut on a word boundary at about 200 characters. Null when the body holds no text. Lets a list row render its two lines without the body being sent at all — see the 'view' parameter. */
  excerpt?: string
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
  /** The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture. */
  thumbnailUrl?: string
  /** Whether the ride is soft-deleted */
  deleted: boolean
  /** Whether the current user is registered in one of this ride's groups. False if anonymous. */
  registered: boolean
  /** ID (TSID) of the group the current user joined, null if not registered */
  registeredGroupId?: string
  /** Whether every group of the ride has reached its capacity. False when the ride has no group, or when at least one group has no maxParticipants. */
  full: boolean
  /** Number of comments, replies included. Absent when the caller may not read the comments of this ride — comments are members-only, so an outsider is told nothing, not even zero. */
  commentCount?: number
}
