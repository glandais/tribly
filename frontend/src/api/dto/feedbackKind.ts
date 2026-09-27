export type FeedbackKind = (typeof FeedbackKind)[keyof typeof FeedbackKind]

export const FeedbackKind = {
  BUG: 'BUG',
  SUGGESTION: 'SUGGESTION',
} as const
