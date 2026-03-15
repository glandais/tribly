import type { AssetDto } from './assetDto'

export interface AssetsDto {
  /** Logo */
  logo?: AssetDto
  /** Images */
  images: AssetDto[]
  /** Attachments */
  attachments: AssetDto[]
  /** Original GPX */
  originalGpx?: AssetDto
  /** GPX */
  gpx?: AssetDto
  /** FIT */
  fit?: AssetDto
  /** Light thumbnail */
  thumbnailLight?: AssetDto
  /** Dark thumbnail */
  thumbnailDark?: AssetDto
}
