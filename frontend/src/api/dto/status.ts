export type Status = (typeof Status)[keyof typeof Status]

export const Status = {
  DRAFT: 'DRAFT',
  PUBLISHED: 'PUBLISHED',
  CANCELLED: 'CANCELLED',
} as const
