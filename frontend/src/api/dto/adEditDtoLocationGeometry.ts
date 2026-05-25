import type { AdEditDtoLocationGeometryType } from './adEditDtoLocationGeometryType.ts'

/**
 * Location coordinates [longitude, latitude]
 */
export type AdEditDtoLocationGeometry = {
  type: AdEditDtoLocationGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
