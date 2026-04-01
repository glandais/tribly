export type TeamDetailDtoGeometryType =
  (typeof TeamDetailDtoGeometryType)[keyof typeof TeamDetailDtoGeometryType]

export const TeamDetailDtoGeometryType = {
  Point: 'Point',
} as const
