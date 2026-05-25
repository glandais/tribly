import type { ClimbDto } from './climbDto.ts'
import type { GeoJsonLineString } from './geoJsonLineString.ts'

/**
 * GPX track with track points
 */
export interface TrackDto {
  line: GeoJsonLineString
  /** List of climbs on the route */
  climbs: ClimbDto[]
}
