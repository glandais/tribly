export type PublicationType = (typeof PublicationType)[keyof typeof PublicationType]

export const PublicationType = {
  RIDE: 'RIDE',
  POST: 'POST',
  TRIP: 'TRIP',
} as const
