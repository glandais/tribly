import type { EntityType } from './entityType'
import type { NotFoundDetailsType } from './notFoundDetailsType'
import type { SearchedBy } from './searchedBy'

export interface NotFoundDetails {
  type: NotFoundDetailsType
  /** Entity type */
  entityType: EntityType
  /** Search type */
  searchedBy: SearchedBy
  /** id/slug */
  id: string
}
