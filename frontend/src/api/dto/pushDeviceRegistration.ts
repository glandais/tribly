import type { PushPlatform } from './pushPlatform.ts'

/**
 * A device to receive push notifications on
 */
export interface PushDeviceRegistration {
  /**
   * The FCM registration token. Registering a token already known moves it to the current user and refreshes its last-seen date.
   * @maxLength 512
   * @pattern \S
   */
  token: string
  /** The device's platform */
  platform: PushPlatform
  /**
   * A human-readable device name, for the member's own device list
   * @maxLength 120
   */
  deviceName?: string
  /**
   * The app version that registered, for support
   * @maxLength 40
   */
  appVersion?: string
}
