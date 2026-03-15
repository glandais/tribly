export type SearchedBy = (typeof SearchedBy)[keyof typeof SearchedBy]

export const SearchedBy = {
  ID: 'ID',
  SLUG: 'SLUG',
} as const
