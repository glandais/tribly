/**
 * One stored thumbnail
 */
export interface ThumbnailFile {
  /** Asset type */
  type: string
  /** Stored size in bytes, -1 when the file is missing */
  bytes: number
}
