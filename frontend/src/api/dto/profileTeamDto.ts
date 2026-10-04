import type { TeamRole } from './teamRole.ts'

/**
 * One of the current user's teams, with the user's role in it
 */
export interface ProfileTeamDto {
  /** Team URL slug */
  slug: string
  /** Team name */
  name: string
  /** The user's role in the team */
  role: TeamRole
}
