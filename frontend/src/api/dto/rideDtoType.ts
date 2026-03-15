export type RideDtoType = (typeof RideDtoType)[keyof typeof RideDtoType]

export const RideDtoType = {
  RIDE: 'RIDE',
} as const
