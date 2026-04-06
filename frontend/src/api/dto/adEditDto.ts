import type { AdEditDtoLocationGeometry } from './adEditDtoLocationGeometry'
import type { AdType } from './adType'
import type { Instant } from './instant'
import type { MediaDto } from './mediaDto'
import type { RentalPeriod } from './rentalPeriod'
import type { Status } from './status'
import type { TeamPublicationDto } from './teamPublicationDto'
import type { Visibility } from './visibility'

/**
 * Ad data
 */
export interface AdEditDto {
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
  /** Location coordinates [longitude, latitude] */
  locationGeometry?: AdEditDtoLocationGeometry
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
