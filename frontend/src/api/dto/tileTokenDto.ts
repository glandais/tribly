import type { Instant } from './instant.ts'

/**
 * Short-lived token authorizing vector tile requests
 */
export interface TileTokenDto {
  /** Token to pass as the 't' query parameter of the .mvt endpoints */
  token: string
  /** Absolute expiry instant (ISO 8601), for logs and diagnostics */
  expiresAt: Instant
  /** Seconds until expiry, measured at issuance. Schedule renewal on this, not on expiresAt: it is immune to a device clock that drifts. */
  expiresIn: number
}
