export type Hilliness = (typeof Hilliness)[keyof typeof Hilliness]

export const Hilliness = {
  FLAT: 'FLAT',
  HILLY: 'HILLY',
  MOUNTAINOUS: 'MOUNTAINOUS',
} as const
