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
  /** Trip start date/time */
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
  /** Number of stages */
  stageCount: number
  /** Trip stages */
  stages: TripStageDto[]
  /** Trip participants */
  participants: PublicUserDto[]
  /** Thumbnail URL (light) */
  thumbnailLightUrl?: string
  /** Thumbnail URL (dark) */
  thumbnailDarkUrl?: string
  /** Whether the trip is soft-deleted */
  deleted: boolean
}
