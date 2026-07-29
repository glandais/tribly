export type GpsServiceType = (typeof GpsServiceType)[keyof typeof GpsServiceType]

export const GpsServiceType = {
  HAMMERHEAD: 'HAMMERHEAD',
  GARMIN: 'GARMIN',
  WAHOO: 'WAHOO',
} as const
