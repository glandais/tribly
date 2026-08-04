/**
 * A place matching a geocoding query
 */
export interface GeocodeResultDto {
  /** Opaque identifier of the result, stable enough to key a list on */
  id: string
  /** Full human-readable name of the place */
  displayName: string
  /** Latitude in degrees (WGS 84) */
  lat: number
  /** Longitude in degrees (WGS 84) */
  lon: number
}
