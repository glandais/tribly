export type ListViewMode = (typeof ListViewMode)[keyof typeof ListViewMode]

export const ListViewMode = {
  FULL: 'FULL',
  COMPACT: 'COMPACT',
} as const
