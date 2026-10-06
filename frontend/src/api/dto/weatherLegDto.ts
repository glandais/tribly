import type { Instant } from './instant.ts'
import type { WeatherCheckpointDto } from './weatherCheckpointDto.ts'
import type { WeatherRainAlertDto } from './weatherRainAlertDto.ts'
import type { WeatherStatus } from './weatherStatus.ts'
import type { WindDto } from './windDto.ts'
import type { WindExposureDto } from './windExposureDto.ts'
import type { WindSegmentDto } from './windSegmentDto.ts'

/**
 * The weather along one ridden route: a group of a ride (a stage of a trip, later). Passages are estimated from startTime at averageSpeed.
 */
export interface WeatherLegDto {
  /** The ride group (TSID). Absent for a ride without groups: the leg rides the ride's own route. */
  groupId?: string
  /** State of this leg's forecast. NO_LOCATION when the leg has no route to sample: then no checkpoint, no segment. */
  status: WeatherStatus
  /** When the leg leaves */
  startTime: Instant
  /** Speed used for the passages, km/h */
  averageSpeed: number
  /** Whether averageSpeed is the 25 km/h default, the group having none — to be said on screen */
  speedIsDefault: boolean
  /** Length of the leg's route, metres */
  distance: number
  /** Estimated arrival */
  arrivalTime: Instant
  /** The oldest fetch among the forecasts this leg reads */
  fetchedAt?: Instant
  /** Forecast points, start to finish */
  checkpoints: WeatherCheckpointDto[]
  /** The wind stretch by stretch, from one checkpoint to the next */
  segments: WindSegmentDto[]
  /** Distance ridden against, across and with the wind */
  windExposure: WindExposureDto
  /** The leg's dominant wind: circular mean of the directions, mean speed, highest gust */
  prevailingWind?: WindDto
  /** The first checkpoint where rain becomes likely, if any */
  rainAlert?: WeatherRainAlertDto
}
