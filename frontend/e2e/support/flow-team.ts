import type {
  MemberListResponse,
  TeamListResponse,
  TeamPageDto,
  TeamPageSummaryDto,
} from '../../src/api/dto'
import { apiGet, withApi, type AuthResponse } from './api'
import { newTeam, setTeamAttributes } from './data'

/** The team journey's reads (flow-team.e2e.ts): what the team screens saved, through the API. */

/** The team's whole roster, as `who` (an admin of the team). */
export const rosterOf = (who: AuthResponse, slug: string) =>
  apiGet<MemberListResponse>(who, `/api/teams/${slug}/members`, { size: 100 })

/** The teams GET /api/teams lists for `who` (anonymous when undefined) under `search`. */
export const listedTeams = (who: AuthResponse | undefined, search: string) =>
  apiGet<TeamListResponse>(who, '/api/teams', { search, size: 50 })

export const pagesOf = (who: AuthResponse, slug: string) =>
  apiGet<TeamPageSummaryDto[]>(who, `/api/teams/${slug}/pages`)

export const pageOf = (who: AuthResponse, slug: string, pageSlug: string) =>
  apiGet<TeamPageDto>(who, `/api/teams/${slug}/pages/${pageSlug}`)

/**
 * A PUBLIC team owned by `owner`, open to join requests from any user of the domain or not
 * (`joinable`: an admin-only attribute, born false, which the platform admin sets).
 */
export async function publicTeam(owner: AuthResponse, name: string, joinable: boolean) {
  const team = await newTeam(owner, name, { visibility: 'PUBLIC' })
  if (joinable) await setTeamAttributes(team, { joinable })
  return team
}

/** POST /api/teams/{slug}/members/join as `who`: the HTTP status, whatever it is. */
export const joinStatus = (who: AuthResponse, slug: string) =>
  withApi(who, async (api) => (await api.post(`/api/teams/${slug}/members/join`)).status())
