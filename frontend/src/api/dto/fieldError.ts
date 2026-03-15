export interface FieldError {
  /** Field */
  field: string
  /** Message */
  message: string
  /** Rejected value */
  rejectedValue: unknown
}
