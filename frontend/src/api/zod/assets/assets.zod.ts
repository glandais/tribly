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

export const UploadAssetResponse = zod.object({
  id: zod.string().describe('ID (TSID)'),
  fileName: zod.string().describe('Filename'),
  contentType: zod.string().describe('Content-Type'),
  url: zod.string().describe('url'),
  imageUrl: zod.string().optional().describe('image template url'),
  imageDimensions: zod
    .object({
      width: zod.int().optional(),
      height: zod.int().optional(),
    })
    .optional()
    .describe('image dimensions'),
})
