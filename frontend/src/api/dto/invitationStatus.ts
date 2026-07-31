export type InvitationStatus = (typeof InvitationStatus)[keyof typeof InvitationStatus]

export const InvitationStatus = {
  PENDING: 'PENDING',
  ACCEPTED: 'ACCEPTED',
  REVOKED: 'REVOKED',
  EXPIRED: 'EXPIRED',
} as const
