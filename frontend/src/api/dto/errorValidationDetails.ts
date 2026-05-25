import type { ErrorValidationDetailsType } from './errorValidationDetailsType.ts'
import type { FieldError } from './fieldError.ts'

export interface ErrorValidationDetails {
  type: ErrorValidationDetailsType
  /** Field Errors */
  fieldErrors: FieldError[]
}
