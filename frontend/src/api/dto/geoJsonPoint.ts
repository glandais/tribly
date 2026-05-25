import type { GeoJsonPointType } from './geoJsonPointType.ts'

/**
 * Location coordinates [longitude, latitude]
 */
export interface GeoJsonPoint {
  type: GeoJsonPointType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
