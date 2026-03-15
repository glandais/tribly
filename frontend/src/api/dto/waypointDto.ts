import type { GeoJsonPoint } from './geoJsonPoint'

export interface WaypointDto {
  geometry: GeoJsonPoint
  name?: string
}
