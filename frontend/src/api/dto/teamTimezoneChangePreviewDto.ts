import type { TeamTimezoneChangeItemDto } from './teamTimezoneChangeItemDto.ts'

/**
 * Preview of a change of the team's zone; nothing is written
 */
export interface TeamTimezoneChangePreviewDto {
  /** Team's current zone */
  from: string
  /** Zone asked for */
  to: string
  /** How many upcoming place-less events keep their wall time */
  upcomingCount: number
  /** How many past place-less events keep their instant, relabelled */
  pastCount: number
  /** The first upcoming ones, soonest first, at most 10 */
  upcoming: TeamTimezoneChangeItemDto[]
}
