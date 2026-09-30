import type { AssetDimensionsDto } from './assetDimensionsDto.ts'

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
  /** Size in bytes of the file a download returns (an image as re-encoded on upload); null while not yet known. Ignored in requests. */
  size?: number
}
