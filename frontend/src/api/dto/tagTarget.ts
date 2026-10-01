export type TagTarget = (typeof TagTarget)[keyof typeof TagTarget]

export const TagTarget = {
  RIDE: 'RIDE',
  POST: 'POST',
  TRIP: 'TRIP',
  ROUTE: 'ROUTE',
  AD: 'AD',
} as const
