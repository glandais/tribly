import * as zod from 'zod'

/**
 * @summary Create asset
 */
export const UploadAssetParams = zod.object({
  teamSlug: zod.string().describe('Team URL slug'),
})

export const UploadAssetQueryParams = zod.object({
  assetType: zod.enum(['LOGO', 'IMAGE', 'ATTACHMENT']).describe('Asset type'),
})

export const UploadAssetBody = zod.object({
  file: zod.instanceof(File).optional(),
})
