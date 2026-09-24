/**
 * User registration request
 */
export interface RegisterRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
  /**
   * Display name
   * @minLength 1
   * @maxLength 200
   * @pattern \S
   */
  displayName: string
  /**
   * Password (min 8 chars)
   * @minLength 8
   * @maxLength 100
   * @pattern \S
   */
  password: string
  /** The member accepted the terms of service. Required, and must be true: the sign-up form asks for it with a checkbox. */
  acceptTerms: boolean
}
