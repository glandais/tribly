export type ThumbnailOwnerKind = (typeof ThumbnailOwnerKind)[keyof typeof ThumbnailOwnerKind]

export const ThumbnailOwnerKind = {
  ROUTE: 'ROUTE',
  RIDE: 'RIDE',
  TRIP: 'TRIP',
} as const
