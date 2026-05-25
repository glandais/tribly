import type { AdType } from './adType.ts'
import type { Instant } from './instant.ts'
import type { MediaDto } from './mediaDto.ts'
import type { RentalPeriod } from './rentalPeriod.ts'
import type { Status } from './status.ts'
import type { TeamPublicationDto } from './teamPublicationDto.ts'
import type { Visibility } from './visibility.ts'

/**
 * Ad data
 */
export interface AdDto {
  /** Team */
  team: TeamPublicationDto
  /** Ad ID (TSID) */
  id: string
  /** Ad URL slug */
  slug: string
  /** Ad name */
  name: string
  /** Ad media */
  media: MediaDto
  /** Ad status */
  status: Status
  /** Visibility level */
  visibility: Visibility
  /** Ad type */
  adType: AdType
  /** Price */
  price?: number
  /** Rental period */
  rentalPeriod?: RentalPeriod
  /** Location description */
  locationDescription?: string
  /** Creation timestamp */
  createdAt: Instant
  /** Creation timestamp */
  updatedAt: Instant
  /** Creator ID (TSID) */
  createdById: string
  /** Whether the ad is soft-deleted */
  deleted: boolean
}
