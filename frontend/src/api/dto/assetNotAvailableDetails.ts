import type { AssetNotAvailableDetailsType } from './assetNotAvailableDetailsType.ts'

export interface AssetNotAvailableDetails {
  type: AssetNotAvailableDetailsType
  /** Id of the asset, as the request cited it */
  assetId: string
}
