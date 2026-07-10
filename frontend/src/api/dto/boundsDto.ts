/**
 * Geographic bounding box, in WGS84 degrees
 */
export interface BoundsDto {
  /** Western edge */
  minLon: number
  /** Southern edge */
  minLat: number
  /** Eastern edge */
  maxLon: number
  /** Northern edge */
  maxLat: number
}
