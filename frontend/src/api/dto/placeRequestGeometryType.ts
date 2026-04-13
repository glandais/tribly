export type PlaceRequestGeometryType =
  (typeof PlaceRequestGeometryType)[keyof typeof PlaceRequestGeometryType]

export const PlaceRequestGeometryType = {
  Point: 'Point',
} as const
