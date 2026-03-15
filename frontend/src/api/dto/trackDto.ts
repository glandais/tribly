import type { ClimbDto } from './climbDto'
import type { GeoJsonLineString } from './geoJsonLineString'

/**
 * GPX track with track points
 */
export interface TrackDto {
  line: GeoJsonLineString
  /** List of climbs on the route */
  climbs: ClimbDto[]
}
