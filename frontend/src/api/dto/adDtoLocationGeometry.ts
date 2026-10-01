import type { AdDtoLocationGeometryType } from './adDtoLocationGeometryType.ts'

/**
 * Approximate location of the ad, deliberately blurred: the point is the centre of a fixed cell about 1 km across, not the seller's address. Enough to tell a nearby ad from a distant one, and the same value on every read so repeated calls cannot be averaged back to the exact position. Null when the ad has no location. The exact point stays on AdEditDto, and only for the seller: the team's admins get this blurred point there too. Proximity filters measure from this blurred point too, never from the exact one.
 */
export type AdDtoLocationGeometry = {
  type: AdDtoLocationGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
