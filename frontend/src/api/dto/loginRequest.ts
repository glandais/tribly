/**
 * Password login request
 */
export interface LoginRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
  /**
   * Password
   * @maxLength 100
   * @pattern \S
   */
  password: string
}
