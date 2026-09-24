export type ReportTargetType = (typeof ReportTargetType)[keyof typeof ReportTargetType]

export const ReportTargetType = {
  COMMENT: 'COMMENT',
  POST: 'POST',
  AD: 'AD',
  RIDE: 'RIDE',
  TRIP: 'TRIP',
  ROUTE: 'ROUTE',
  MEMBER: 'MEMBER',
} as const
