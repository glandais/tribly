export type MinRole = (typeof MinRole)[keyof typeof MinRole]

export const MinRole = {
  MEMBER: 'MEMBER',
  ORGANIZER: 'ORGANIZER',
  ADMIN: 'ADMIN',
} as const
