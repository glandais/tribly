export type RelativeWind = (typeof RelativeWind)[keyof typeof RelativeWind]

export const RelativeWind = {
  HEAD: 'HEAD',
  CROSS: 'CROSS',
  TAIL: 'TAIL',
} as const
