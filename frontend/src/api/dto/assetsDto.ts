import type { AssetDto } from './assetDto.ts'

/**
 * Assets of a content. On a write, the logo, images and attachments must each be an asset uploaded to this team and attached to no other content, else 400 ASSET_NOT_AVAILABLE naming the asset id — the same answer whether the id is unknown, of another team or held by another content.
 */
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
