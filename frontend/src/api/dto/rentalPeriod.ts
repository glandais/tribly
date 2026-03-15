export type RentalPeriod = (typeof RentalPeriod)[keyof typeof RentalPeriod]

export const RentalPeriod = {
  DAY: 'DAY',
  WEEK: 'WEEK',
  MONTH: 'MONTH',
} as const
