/**
 * Activates an account from its verification link: the password is chosen here, by whoever holds the mailbox, never at sign-up.
 */
export interface ActivateAccountRequest {
  /**
   * Verification token
   * @maxLength 100
   * @pattern \S
   */
  token: string
  /**
   * Password (min 8 chars)
   * @minLength 8
   * @maxLength 100
   * @pattern \S
   */
  password: string
}
