import type { ListViewMode } from './listViewMode.ts'
import type { Status } from './status.ts'

export type ListMyParticipationsParams = {
  /**
   * Start date filter (ISO format)
   */
  from?: string
  /**
   * Page number
   */
  page?: number
  /**
   * Page size
   */
  size?: number
  /**
   * Only publications with this status
   */
  status?: Status
  /**
   * End date filter (ISO format)
   */
  to?: string
  /**
   * How much of each row to send. COMPACT (case-insensitive) returns media.markdown empty and media.assets empty — read 'excerpt' and 'thumbnailUrl' instead, both of which are present either way. Omitted, or FULL, is the previous behaviour, byte for byte.
   */
  view?: ListViewMode
}
