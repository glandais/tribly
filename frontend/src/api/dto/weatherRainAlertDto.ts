import type { Instant } from './instant.ts'
import type { WeatherCondition } from './weatherCondition.ts'

/**
 * The first moment rain becomes likely: a probability of 50 % or more
 */
export interface WeatherRainAlertDto {
  /** Probability of precipitation then, % (0–100) */
  probability: number
  /** When — the passage at the checkpoint, or the hour */
  time: Instant
  /** Where, metres from the start of the leg. Absent in a ride's summary, which only knows the departure point. */
  distance?: number
  /** The condition forecast then */
  condition: WeatherCondition
}
