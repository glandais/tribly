import type { ErrorValidationDetailsType } from './errorValidationDetailsType'
import type { FieldError } from './fieldError'

export interface ErrorValidationDetails {
  type: ErrorValidationDetailsType
  /** Field Errors */
  fieldErrors: FieldError[]
}
