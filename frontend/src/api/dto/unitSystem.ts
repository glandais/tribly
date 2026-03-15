export type UnitSystem = (typeof UnitSystem)[keyof typeof UnitSystem]

export const UnitSystem = {
  METRIC: 'METRIC',
  IMPERIAL: 'IMPERIAL',
} as const
