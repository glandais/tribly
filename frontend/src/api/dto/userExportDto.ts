import type { Instant } from './instant.ts'
import type { UserExportStatus } from './userExportStatus.ts'

/**
 * Status of a personal data export request
 */
export interface UserExportDto {
  /** Export job identifier */
  id: string
  /** Current status */
  status: UserExportStatus
  /** When the export was requested */
  requestedAt: Instant
  /** When the export finished building */
  completedAt?: Instant
  /** When the download link stops working */
  expiresAt?: Instant
  /** Size of the archive in bytes */
  fileSize?: number
}
