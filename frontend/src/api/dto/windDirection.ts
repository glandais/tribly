export type WindDirection = (typeof WindDirection)[keyof typeof WindDirection]

export const WindDirection = {
  NORTH: 'NORTH',
  NORTH_EAST: 'NORTH_EAST',
  EAST: 'EAST',
  SOUTH_EAST: 'SOUTH_EAST',
  SOUTH: 'SOUTH',
  SOUTH_WEST: 'SOUTH_WEST',
  WEST: 'WEST',
  NORTH_WEST: 'NORTH_WEST',
} as const
