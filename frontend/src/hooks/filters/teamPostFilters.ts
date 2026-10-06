import { z } from 'zod'
import { ListViewMode, PublicationType } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField, tagIdsField } from './common'

export const TEAM_POST_PAGE_SIZE = 12

/**
 * The filters of a team's « Publications » (`teamPosts`): a search and the post tags. No time
 * scope — a post reads like a blog entry, newest first (plan 2026-10-06 §2).
 */
export const teamPostFiltersSchema = z.object({
  search: searchField,
  tags: tagIdsField,
  page: pageField,
  size: sizeField(TEAM_POST_PAGE_SIZE),
})

export type TeamPostFilters = z.infer<typeof teamPostFiltersSchema>

export const teamPostFiltersAlias = COMMON_ALIAS

/**
 * Projects the posts list's filters onto `GET /api/teams/{slug}/publications`, for the page and
 * its route `prefetch` alike. The list endpoint's default order — latest first — is the one
 * wanted.
 */
export function teamPostApiParams(filters: TeamPostFilters) {
  return {
    search: filters.search,
    page: filters.page,
    size: filters.size,
    type: PublicationType.POST,
    ...(filters.tags ? { tags: filters.tags } : {}),
    view: ListViewMode.COMPACT,
  }
}
