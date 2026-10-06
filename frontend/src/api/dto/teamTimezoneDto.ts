/**
 * The zone of a point, else the team's
 */
export interface TeamTimezoneDto {
  /** IANA zone of the point; the team's own when no point is given or the point lies outside every zone */
  timezone: string
}
