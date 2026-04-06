export type GeoJsonLineStringType =
  (typeof GeoJsonLineStringType)[keyof typeof GeoJsonLineStringType]

export const GeoJsonLineStringType = {
  LineString: 'LineString',
} as const
