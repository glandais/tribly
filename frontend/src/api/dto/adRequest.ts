import type { AdRequestLocationGeometry } from './adRequestLocationGeometry.ts'
import type { AdType } from './adType.ts'
import type { MediaDto } from './mediaDto.ts'
import type { RentalPeriod } from './rentalPeriod.ts'
import type { Status } from './status.ts'

/**
 * Ad request
 */
export interface AdRequest {
  /**
   * Ad name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /** Ad description */
  media: MediaDto
  /** Ad status */
  status: Status
  /** Ad type */
  adType: AdType
  /** Price (optional, null for 'contact for price') */
  price?: number
  /** Rental period (required for RENTAL type) */
  rentalPeriod?: RentalPeriod
  /**
   * Location description
   * @maxLength 200
   */
  locationDescription?: string
  /** Location coordinates [longitude, latitude] */
  locationGeometry?: AdRequestLocationGeometry
  /** IDs (TSID) of the team's AD tags the ad carries, replacing the whole set — at most 10, each a tag of this team and of kind AD, else 400 (TAG_INVALID, TOO_MANY_TAGS). An empty list removes them all. Omitted: none on a creation, left as they are on an update. */
  tagIds?: string[]
}
