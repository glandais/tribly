import type { Instant } from './instant.ts'
import type { PairedDeviceType } from './pairedDeviceType.ts'

/**
 * A device (Karoo, Garmin watch) paired with the account by code
 */
export interface PairedDeviceDto {
  /** Pairing ID, to unpair the device */
  id: string
  /** Kind of device */
  type: PairedDeviceType
  /** When the device was paired */
  pairedAt: Instant
  /** When the device last renewed its access */
  lastUsedAt?: Instant
}
