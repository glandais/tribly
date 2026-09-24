export type ReportReason = (typeof ReportReason)[keyof typeof ReportReason]

export const ReportReason = {
  SPAM: 'SPAM',
  HARASSMENT: 'HARASSMENT',
  HATE: 'HATE',
  SEXUAL: 'SEXUAL',
  VIOLENCE: 'VIOLENCE',
  ILLEGAL: 'ILLEGAL',
  INAPPROPRIATE_IMAGE: 'INAPPROPRIATE_IMAGE',
  OTHER: 'OTHER',
} as const
