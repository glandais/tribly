export type RouteSortBy = (typeof RouteSortBy)[keyof typeof RouteSortBy]

export const RouteSortBy = {
  DISTANCE: 'DISTANCE',
  ELEVATION_GAIN: 'ELEVATION_GAIN',
  HILLINESS: 'HILLINESS',
  DATE_TIME: 'DATE_TIME',
} as const
