import type { GeoJsonLineString } from './geoJsonLineString.ts'

export interface RouterResponse {
  route: GeoJsonLineString
  dist?: number
  ascend?: number
}
