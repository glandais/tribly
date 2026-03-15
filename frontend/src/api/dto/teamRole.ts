export type TeamRole = (typeof TeamRole)[keyof typeof TeamRole]

export const TeamRole = {
  MEMBER: 'MEMBER',
  ORGANIZER: 'ORGANIZER',
  ADMIN: 'ADMIN',
} as const
