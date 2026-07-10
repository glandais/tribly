import { z } from 'zod'
import { TeamRole } from '@/api/dto'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const TEAM_MEMBER_PAGE_SIZE = 50

export const teamMemberFiltersSchema = z.object({
  search: searchField,
  role: z.enum(TeamRole).optional().catch(undefined),
  page: pageField,
  size: sizeField(TEAM_MEMBER_PAGE_SIZE),
})

export const teamMemberFiltersAlias = COMMON_ALIAS
