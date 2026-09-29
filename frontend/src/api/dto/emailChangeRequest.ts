/**
 * Request to change the signed-in user's email address
 */
export interface EmailChangeRequest {
  /**
   * New email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
}
