import type { GeoJsonPointType } from './geoJsonPointType'

/**
 * Location coordinates [longitude, latitude]
 */
export interface GeoJsonPoint {
  type: GeoJsonPointType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
