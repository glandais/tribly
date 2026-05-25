import type { AdRequestLocationGeometryType } from './adRequestLocationGeometryType.ts'

/**
 * Location coordinates [longitude, latitude]
 */
export type AdRequestLocationGeometry = {
  type: AdRequestLocationGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
