export type ReportStatus = (typeof ReportStatus)[keyof typeof ReportStatus]

export const ReportStatus = {
  OPEN: 'OPEN',
  REMOVED: 'REMOVED',
  DISMISSED: 'DISMISSED',
} as const
