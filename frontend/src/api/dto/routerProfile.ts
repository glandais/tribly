export type RouterProfile = (typeof RouterProfile)[keyof typeof RouterProfile]

export const RouterProfile = {
  BIKE: 'BIKE',
  FASTBIKE: 'FASTBIKE',
  GRAVEL: 'GRAVEL',
  MTB: 'MTB',
  RUN_HIKE: 'RUN_HIKE',
  MOTORCYCLE: 'MOTORCYCLE',
} as const
