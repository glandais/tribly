export type ReportQueueStatus = (typeof ReportQueueStatus)[keyof typeof ReportQueueStatus]

export const ReportQueueStatus = {
  OPEN: 'OPEN',
  RESOLVED: 'RESOLVED',
} as const
