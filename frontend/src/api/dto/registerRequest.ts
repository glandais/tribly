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
}
