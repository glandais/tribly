export type PairedDeviceType = (typeof PairedDeviceType)[keyof typeof PairedDeviceType]

export const PairedDeviceType = {
  KAROO: 'KAROO',
  GARMIN: 'GARMIN',
  OTHER: 'OTHER',
} as const
