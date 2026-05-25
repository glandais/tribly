import type { ErrorCode } from './errorCode.ts'
import type { ErrorDetails } from './errorDetails.ts'

/**
 * Error response
 */
export interface ErrorResponse {
  /** Error code */
  code: ErrorCode
  /** Additional details */
  errorDetails?: ErrorDetails
}
