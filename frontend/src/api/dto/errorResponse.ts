import type { ErrorCode } from './errorCode'
import type { ErrorDetails } from './errorDetails'

/**
 * Error response
 */
export interface ErrorResponse {
  /** Error code */
  code: ErrorCode
  /** Additional details */
  errorDetails?: ErrorDetails
}
