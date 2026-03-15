import type { AdRequestLocationGeometry } from './adRequestLocationGeometry'
import type { AdType } from './adType'
import type { MediaDto } from './mediaDto'
import type { RentalPeriod } from './rentalPeriod'
import type { Status } from './status'

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
}
