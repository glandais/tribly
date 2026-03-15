export type GeoJsonPointType = (typeof GeoJsonPointType)[keyof typeof GeoJsonPointType]

export const GeoJsonPointType = {
  Point: 'Point',
} as const
