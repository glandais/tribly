import type { AdEditDtoLocationGeometryType } from './adEditDtoLocationGeometryType'

/**
 * Location coordinates [longitude, latitude]
 */
export type AdEditDtoLocationGeometry = {
  type: AdEditDtoLocationGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
