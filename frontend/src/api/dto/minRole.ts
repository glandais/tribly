export type MinRole = (typeof MinRole)[keyof typeof MinRole]

export const MinRole = {
  NOT_MEMBER: 'NOT_MEMBER',
  MEMBER: 'MEMBER',
  ORGANIZER: 'ORGANIZER',
  ADMIN: 'ADMIN',
} as const
