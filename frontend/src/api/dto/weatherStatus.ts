export type WeatherStatus = (typeof WeatherStatus)[keyof typeof WeatherStatus]

export const WeatherStatus = {
  OK: 'OK',
  STALE: 'STALE',
  NOT_YET_AVAILABLE: 'NOT_YET_AVAILABLE',
  UNAVAILABLE: 'UNAVAILABLE',
  NO_LOCATION: 'NO_LOCATION',
  OUT_OF_RANGE: 'OUT_OF_RANGE',
} as const
