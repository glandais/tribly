export type NearType = (typeof NearType)[keyof typeof NearType]

export const NearType = {
  START: 'START',
  END: 'END',
  START_OR_END: 'START_OR_END',
} as const
