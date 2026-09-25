import type {
  MemberListResponse,
  TeamListResponse,
  TeamPageDto,
  TeamPageSummaryDto,
} from '../../src/api/dto'
import { apiGet, type AuthResponse } from './api'

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
