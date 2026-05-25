import type { EntityType } from './entityType.ts'
import type { NotFoundDetailsType } from './notFoundDetailsType.ts'
import type { SearchedBy } from './searchedBy.ts'

export interface NotFoundDetails {
  type: NotFoundDetailsType
  /** Entity type */
  entityType: EntityType
  /** Search type */
  searchedBy: SearchedBy
  /** id/slug */
  id: string
}
