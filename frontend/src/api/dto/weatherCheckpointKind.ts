export type WeatherCheckpointKind =
  (typeof WeatherCheckpointKind)[keyof typeof WeatherCheckpointKind]

export const WeatherCheckpointKind = {
  START: 'START',
  EN_ROUTE: 'EN_ROUTE',
  FINISH: 'FINISH',
} as const
