export type TripDtoType = (typeof TripDtoType)[keyof typeof TripDtoType]

export const TripDtoType = {
  TRIP: 'TRIP',
} as const
