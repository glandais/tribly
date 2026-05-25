import type { Instant } from './instant.ts'

/**
 * Passkey information
 */
export interface PasskeyDto {
  /** Passkey ID */
  id?: string
  /** Credential ID (base64url) */
  credentialId?: string
  /** Device name */
  deviceName?: string
  /** Transport methods */
  transports?: string[]
  /** Created timestamp */
  createdAt?: Instant
  /** Last used timestamp */
  lastUsedAt?: Instant
}
