import type { GeoJsonLineStringType } from './geoJsonLineStringType.ts'

export interface GeoJsonLineString {
  type: GeoJsonLineStringType
  /** Array of [lon, lat] coordinates */
  coordinates: number[][]
}
