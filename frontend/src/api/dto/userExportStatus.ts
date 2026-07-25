export type UserExportStatus = (typeof UserExportStatus)[keyof typeof UserExportStatus]

export const UserExportStatus = {
  PENDING: 'PENDING',
  PROCESSING: 'PROCESSING',
  READY: 'READY',
  FAILED: 'FAILED',
  EXPIRED: 'EXPIRED',
} as const
