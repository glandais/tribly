import { z } from 'zod'
import { COMMON_ALIAS, pageField, searchField, sizeField } from './common'

export const RIDE_TEMPLATE_PAGE_SIZE = 20

export const rideTemplateFiltersSchema = z.object({
  search: searchField,
  page: pageField,
  size: sizeField(RIDE_TEMPLATE_PAGE_SIZE),
})

export const rideTemplateFiltersAlias = COMMON_ALIAS
