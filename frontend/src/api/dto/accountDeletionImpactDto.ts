import type { TeamPublicationDto } from './teamPublicationDto.ts'

/**
 * What deleting the current user's account would do to their teams
 */
export interface AccountDeletionImpactDto {
  /** Whether the deletion is refused (SOLE_TEAM_ADMIN): the user is the only admin of at least one team that has other members */
  blocked: boolean
  /** Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account */
  blockingTeams: TeamPublicationDto[]
  /** Teams the user administers and is the only member of; they are deleted with the account */
  deletedTeams: TeamPublicationDto[]
}
