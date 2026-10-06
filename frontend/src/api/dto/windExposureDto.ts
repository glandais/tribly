/**
 * How much of a leg rides against, across and with the wind, metres. All zero when no stretch has weather.
 */
export interface WindExposureDto {
  /** Metres with a HEAD wind */
  head: number
  /** Metres with a CROSS wind */
  cross: number
  /** Metres with a TAIL wind */
  tail: number
}
