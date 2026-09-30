/**
 * A device to stop sending push notifications to
 */
export interface PushDeviceUnregistration {
  /**
   * The FCM registration token to drop
   * @maxLength 512
   * @pattern \S
   */
  token: string
}
