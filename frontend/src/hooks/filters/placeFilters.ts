import { z } from 'zod'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const PLACE_PAGE_SIZE = 20

export const placeFiltersSchema = z.object({
  search: searchField,
  page: pageField,
  size: sizeField(PLACE_PAGE_SIZE),
})

export const placeFiltersAlias = COMMON_ALIAS
