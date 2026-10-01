import type { AdEditDtoLocationGeometryType } from './adEditDtoLocationGeometryType.ts'

/**
 * Location coordinates [longitude, latitude]. Exact for the ad's author only; any other editor (a team admin, a platform admin) gets the same blurred point as AdDto, the centre of a cell about 1 km across. Sending that blurred point back unchanged in an update by a non-author keeps the stored exact point; any other value replaces it.
 */
export type AdEditDtoLocationGeometry = {
  type: AdEditDtoLocationGeometryType
  /** Coordinates [longitude, latitude] */
  coordinates: number[]
}
