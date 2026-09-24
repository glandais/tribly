import type { TeamPublicationDto } from './teamPublicationDto.ts'

/**
 * What deleting the current user's account would do to their teams
 */
export interface AccountDeletionImpactDto {
  /** Whether the deletion is refused: the user is the only admin of at least one team that has other members (SOLE_TEAM_ADMIN), or of a team migrated from biketeam (SOLE_MIGRATED_TEAM_ADMIN) */
  blocked: boolean
  /** Teams the user is the only admin of while other members remain; they must name another admin, or delete the team, before deleting their account. Excludes the teams listed in migratedTeams */
  blockingTeams: TeamPublicationDto[]
  /** Teams the user administers and is the only member of; they are deleted with the account. Excludes the teams listed in migratedTeams */
  deletedTeams: TeamPublicationDto[]
  /** Teams the user is the only admin of, with or without other members, that were migrated from biketeam: their old biketeam addresses redirect to them. Each one refuses the deletion until another admin is named (or, for a platform admin, the switch-over is cancelled on biketeam and the team deleted) */
  migratedTeams: TeamPublicationDto[]
}
