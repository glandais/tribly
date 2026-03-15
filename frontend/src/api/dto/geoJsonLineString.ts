import type { GeoJsonLineStringType } from './geoJsonLineStringType'

export interface GeoJsonLineString {
  type: GeoJsonLineStringType
  /** Array of [lon, lat] coordinates */
  coordinates: number[][]
}
