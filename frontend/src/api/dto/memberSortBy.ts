export type MemberSortBy = (typeof MemberSortBy)[keyof typeof MemberSortBy]

export const MemberSortBy = {
  JOINED_AT: 'JOINED_AT',
} as const
