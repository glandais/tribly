import type { ErrorCode } from './errorCode'
import type { ErrorValidationDetails } from './errorValidationDetails'
import type { NotFoundDetails } from './notFoundDetails'

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
