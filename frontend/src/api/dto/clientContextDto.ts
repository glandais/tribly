import type { ClientPlatform } from './clientPlatform.ts'

/**
 * Where a feedback or an error report comes from: client, device, screen
 */
export interface ClientContextDto {
  /** The client */
  platform: ClientPlatform
  /**
   * Version of the client, e.g. 1.0.0 or a git commit
   * @maxLength 50
   * @pattern \S
   */
  appVersion: string
  /**
   * Build number of a mobile client
   * @maxLength 50
   */
  buildNumber?: string
  /**
   * OS name and version, e.g. Android 15
   * @maxLength 100
   */
  osVersion?: string
  /**
   * Device model
   * @maxLength 200
   */
  device?: string
  /**
   * Browser user agent
   * @maxLength 500
   */
  userAgent?: string
  /**
   * Path of the current page or screen, without its query string
   * @maxLength 500
   */
  route?: string
  /**
   * UI language, e.g. fr
   * @maxLength 20
   */
  locale?: string
  /**
   * IANA time zone, e.g. Europe/Paris
   * @maxLength 64
   */
  timezone?: string
  /**
   * Slug of the team being browsed, if any
   * @maxLength 100
   */
  teamSlug?: string
}
