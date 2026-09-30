import type { Status } from './status.ts'

/**
 * Status change request
 */
export interface StatusChangeRequest {
  /** New status */
  status: Status
}
