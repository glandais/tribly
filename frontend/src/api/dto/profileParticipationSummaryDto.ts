import type { PublicationDto } from './publicationDto.ts'

/**
 * The rides and trips the current user is registered to, summed up
 */
export interface ProfileParticipationSummaryDto {
  /** Outings starting from now on */
  upcomingCount: number
  /** Outings that started before now */
  pastCount: number
  /** The next outing, in the compact list view (no markdown body): empty when nothing is coming up, never more than one row */
  next: PublicationDto[]
}
