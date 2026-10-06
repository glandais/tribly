export type CompassPoint = (typeof CompassPoint)[keyof typeof CompassPoint]

export const CompassPoint = {
  N: 'N',
  NE: 'NE',
  E: 'E',
  SE: 'SE',
  S: 'S',
  SW: 'SW',
  W: 'W',
  NW: 'NW',
} as const
