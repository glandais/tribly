export type ClimbCategory = (typeof ClimbCategory)[keyof typeof ClimbCategory]

export const ClimbCategory = {
  HC: 'HC',
  CAT1: 'CAT1',
  CAT2: 'CAT2',
  CAT3: 'CAT3',
  CAT4: 'CAT4',
} as const
