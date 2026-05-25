import type { GeoJsonPoint } from './geoJsonPoint.ts'

export interface WaypointDto {
  geometry: GeoJsonPoint
  name?: string
}
