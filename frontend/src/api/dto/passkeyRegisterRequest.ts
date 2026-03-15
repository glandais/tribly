/**
 * Request to register a new passkey with device name
 */
export interface PasskeyRegisterRequest {
  /**
   * Optional device name for this passkey
   * @maxLength 250
   */
  deviceName?: string
}
