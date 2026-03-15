import type { GeoJsonLineString } from './geoJsonLineString'

export interface RouterResponse {
  route: GeoJsonLineString
  dist?: number
  ascend?: number
}
