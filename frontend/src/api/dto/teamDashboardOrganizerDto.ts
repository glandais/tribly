import type { PublicationListResponse } from './publicationListResponse.ts'
import type { RideTemplateListResponse } from './rideTemplateListResponse.ts'
import type { TeamDashboardReportsDto } from './teamDashboardReportsDto.ts'

/**
 * The organizer part of a team dashboard: the « À traiter » tiles and the ride templates. Each list is a short page (at most 5 rows); its total is the tile's figure.
 */
export interface TeamDashboardOrganizerDto {
  /** The team's drafts (rides, posts, trips), newest first. total is the number of drafts, the rows their names. */
  drafts: PublicationListResponse
  /** Published rides starting from now routed nowhere — neither the ride nor any of its groups has a route — soonest first. Same rows as GET …/publications?type=RIDE&withoutRoute=true. Null when rides are disabled. */
  ridesWithoutRoute?: PublicationListResponse
  /** Published rides starting from now with at least one group at capacity, soonest first. Same rows as GET …/publications?type=RIDE&withFullGroup=true. Null when rides are disabled. */
  ridesWithFullGroup?: PublicationListResponse
  /** The open reports of the team's moderation queue */
  reports: TeamDashboardReportsDto
  /** « Créer depuis un modèle »: the team's ride templates (at most 5), each with its groupCount. Null when rides are disabled. */
  rideTemplates?: RideTemplateListResponse
}
