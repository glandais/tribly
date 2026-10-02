export type GpsConnectReturn = (typeof GpsConnectReturn)[keyof typeof GpsConnectReturn]

export const GpsConnectReturn = {
  PROFILE: 'PROFILE',
  DEVICE_KAROO: 'DEVICE_KAROO',
} as const
