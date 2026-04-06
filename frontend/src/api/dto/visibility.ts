export type Visibility = (typeof Visibility)[keyof typeof Visibility]

export const Visibility = {
  TEAM: 'TEAM',
  PUBLIC_UNLISTED: 'PUBLIC_UNLISTED',
  PUBLIC: 'PUBLIC',
} as const
