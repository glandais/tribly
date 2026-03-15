/**
 * OTP request
 */
export interface OtpRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
}
