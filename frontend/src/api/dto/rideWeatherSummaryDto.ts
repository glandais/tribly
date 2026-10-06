import type { Instant } from './instant.ts'
import type { WeatherCondition } from './weatherCondition.ts'
import type { WeatherRainAlertDto } from './weatherRainAlertDto.ts'
import type { WeatherStatus } from './weatherStatus.ts'
import type { WindDto } from './windDto.ts'

/**
 * A ride's weather on a card, at the meeting point, over the window from the departure to the estimated arrival of the last group — or a trip stage's, over its checkpoints. Only OK, STALE and NOT_YET_AVAILABLE are ever sent; for NOT_YET_AVAILABLE only status and availableFrom are set.
 */
export interface RideWeatherSummaryDto {
  /** OK, STALE or NOT_YET_AVAILABLE */
  status: WeatherStatus
  /** For NOT_YET_AVAILABLE: when the forecast opens, seven days before the departure */
  availableFrom?: Instant
  /** WMO code at the departure hour */
  weatherCode?: number
  /** weatherCode folded into a condition, same table as WeatherConditionsDto.condition */
  condition?: WeatherCondition
  /** Whether the departure hour is between sunrise and sunset */
  daylight?: boolean
  /** Air temperature at the departure hour, °C */
  temperature?: number
  /** Lowest temperature over the window, °C */
  temperatureMin?: number
  /** Highest temperature over the window, °C */
  temperatureMax?: number
  /** Highest probability of precipitation over the window, %. Absent when the model gives none. */
  maxPrecipitationProbability?: number
  /** Wind at the departure hour */
  wind?: WindDto
  /** The first hour of the window with rain likely (50 % or more). Its distance is absent on a ride's summary, present on a trip stage's (the checkpoint's) */
  rainAlert?: WeatherRainAlertDto
}
