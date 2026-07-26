import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { PublicUserDto } from './publicUserDto.ts'
import type { Status } from './status.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { TripDtoType } from './tripDtoType.ts'
import type { TripStageDto } from './tripStageDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Trip data
 */
export interface TripDto {
  type: TripDtoType
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
  /** Trip start date/time */
  dateTime: Instant
  /** Date of the last stage — the day the trip ends. Null when the trip has no stage, in which case it lasts a day and dateTime is both ends. */
  endDate?: Instant
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
  /** Number of stages */
  stageCount: number
  /** Distance in metres over every stage that has a route. Null when no stage has one — an unrouted trip has no distance, which is not the same as a distance of zero. */
  totalDistance?: number
  /** Elevation gain in metres over every stage that has a route. Null when no stage has one. */
  totalElevationGain?: number
  /** Trip stages */
  stages: TripStageDto[]
  /** Trip participants */
  participants: PublicUserDto[]
  /** Thumbnail URL (light) */
  thumbnailLightUrl?: string
  /** Thumbnail URL (dark) */
  thumbnailDarkUrl?: string
  /** The one thumbnail to show when the client does not theme its cards: the light variant if there is one, else the dark one. Saves a compact row from carrying media.assets just to find a picture. */
  thumbnailUrl?: string
  /** Whether the trip is soft-deleted */
  deleted: boolean
  /** Whether the current user is registered for this trip. False if anonymous. */
  registered: boolean
  /** Number of comments, replies included. Absent when the caller may not read the comments of this trip — comments are members-only, so an outsider is told nothing, not even zero. */
  commentCount?: number
}
