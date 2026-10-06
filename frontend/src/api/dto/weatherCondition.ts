export type WeatherCondition = (typeof WeatherCondition)[keyof typeof WeatherCondition]

export const WeatherCondition = {
  CLEAR: 'CLEAR',
  MOSTLY_CLEAR: 'MOSTLY_CLEAR',
  PARTLY_CLOUDY: 'PARTLY_CLOUDY',
  OVERCAST: 'OVERCAST',
  FOG: 'FOG',
  DRIZZLE: 'DRIZZLE',
  RAIN: 'RAIN',
  HEAVY_RAIN: 'HEAVY_RAIN',
  FREEZING_RAIN: 'FREEZING_RAIN',
  SHOWERS: 'SHOWERS',
  SNOW: 'SNOW',
  THUNDERSTORM: 'THUNDERSTORM',
} as const
