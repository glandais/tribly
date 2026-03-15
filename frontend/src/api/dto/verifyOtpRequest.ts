/**
 * OTP verification request
 */
export interface VerifyOtpRequest {
  /**
   * Email address
   * @maxLength 250
   * @pattern \S
   */
  email: string
  /**
   * 6-digit OTP code
   * @pattern ^\d{6}$
   */
  code: string
}
