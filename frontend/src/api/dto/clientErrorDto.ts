/**
 * An unhandled error caught by a client
 */
export interface ClientErrorDto {
  /**
   * Error class, e.g. TypeError or _TypeError
   * @maxLength 200
   * @pattern \S
   */
  type: string
  /**
   * Error message
   * @maxLength 2000
   */
  message: string
  /**
   * Stack trace, as the client printed it
   * @maxLength 16000
   */
  stack?: string
}
