export type Visibility = (typeof Visibility)[keyof typeof Visibility]

export const Visibility = {
  TEAM: 'TEAM',
  PUBLIC: 'PUBLIC',
} as const
