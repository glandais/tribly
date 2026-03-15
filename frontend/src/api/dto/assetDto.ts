import type { AssetDimensionsDto } from './assetDimensionsDto'

export interface AssetDto {
  /** ID (TSID) */
  id: string
  /** Filename */
  fileName: string
  /** Content-Type */
  contentType: string
  /** url */
  url: string
  /** image template url */
  imageUrl?: string
  /** image dimensions */
  imageDimensions?: AssetDimensionsDto
}
