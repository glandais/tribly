import type { TeamRequestGeometryType } from './teamRequestGeometryType'

/**
 * Team location coordinates [longitude, latitude]
 */
export type TeamRequestGeometry = {
  type: TeamRequestGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
