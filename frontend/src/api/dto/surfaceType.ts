export type SurfaceType = (typeof SurfaceType)[keyof typeof SurfaceType]

export const SurfaceType = {
  ROAD: 'ROAD',
  GRAVEL: 'GRAVEL',
  MTB: 'MTB',
  MIXED: 'MIXED',
} as const
