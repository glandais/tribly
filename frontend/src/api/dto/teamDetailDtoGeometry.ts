import type { TeamDetailDtoGeometryType } from './teamDetailDtoGeometryType.ts'

/**
 * Team location coordinates [longitude, latitude]
 */
export type TeamDetailDtoGeometry = {
  type: TeamDetailDtoGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
