import type { ErrorCode } from './errorCode.ts'
import type { ErrorValidationDetails } from './errorValidationDetails.ts'
import type { NotFoundDetails } from './notFoundDetails.ts'

/**
 * Error details
 */
export type ErrorDetails =
  | (ErrorValidationDetails & {
      type?: ErrorCode
    })
  | (NotFoundDetails & {
      type?: ErrorCode
    })
