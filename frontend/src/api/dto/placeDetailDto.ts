import type { GeoJsonPoint } from './geoJsonPoint'

export interface PlaceDetailDto {
  /** Place ID (TSID) */
  id: string
  name: string
  address?: string
  link?: string
  startPlace: boolean
  endPlace: boolean
  geometry?: GeoJsonPoint
}
