/**
 * Request to set/change the account's real email address
 */
export interface EmailChangeRequest {
  /**
   * New email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
}
