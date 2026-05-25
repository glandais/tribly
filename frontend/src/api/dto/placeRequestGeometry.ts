import type { PlaceRequestGeometryType } from './placeRequestGeometryType.ts'

/**
 * Geographic coordinates [longitude, latitude]
 */
export type PlaceRequestGeometry = {
  type: PlaceRequestGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
