export type NotFoundDetailsType = (typeof NotFoundDetailsType)[keyof typeof NotFoundDetailsType]

export const NotFoundDetailsType = {
  NOT_FOUND: 'NOT_FOUND',
} as const
