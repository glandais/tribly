export type TeamSortBy = (typeof TeamSortBy)[keyof typeof TeamSortBy]

export const TeamSortBy = {
  NAME: 'NAME',
  MEMBER_COUNT: 'MEMBER_COUNT',
} as const
