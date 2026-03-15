import type { PlaceRequestGeometry } from './placeRequestGeometry'

/**
 * Place create/update request
 */
export interface PlaceRequest {
  /**
   * Place name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  name: string
  /**
   * Address
   * @minLength 3
   * @maxLength 200
   */
  address?: string
  /**
   * External link (e.g., Google Maps URL)
   * @minLength 3
   * @maxLength 200
   */
  link?: string
  /** Can be used as ride start point */
  startPlace: boolean
  /** Can be used as ride end point */
  endPlace: boolean
  /** Geographic coordinates [longitude, latitude] */
  geometry?: PlaceRequestGeometry
}
