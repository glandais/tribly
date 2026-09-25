import { crc32, deflateSync } from 'node:zlib'
import type { AdDto, AdRequest, AssetDto } from '../../src/api/dto'
import { apiGet, withApi, apiPost, expectOk, type AuthResponse } from './api'

/**
 * Ads (classifieds) seeded through the REST API: pictures uploaded as team assets, then an ad whose
 * markdown references them — the backend only keeps the images a `::asset{id="…"}` directive points at
 * (AssetService.updateAssets), exactly as the editor writes them.
 */

/** A solid-colour PNG, `width` × `height`, encoded by hand so the suite needs no image library. */
export function solidPng(width: number, height: number, [r, g, b]: [number, number, number]) {
  const chunk = (type: string, data: Buffer) => {
    const length = Buffer.alloc(4)
    length.writeUInt32BE(data.length)
    const body = Buffer.concat([Buffer.from(type, 'ascii'), data])
    const crc = Buffer.alloc(4)
    crc.writeUInt32BE(crc32(body))
    return Buffer.concat([length, body, crc])
  }
  const header = Buffer.alloc(13)
  header.writeUInt32BE(width, 0)
  header.writeUInt32BE(height, 4)
  header[8] = 8 // bit depth
  header[9] = 2 // truecolour RGB
  const row = Buffer.alloc(1 + width * 3) // filter byte 0, then pixels
  for (let x = 0; x < width; x++) row.set([r, g, b], 1 + x * 3)
  const raw = Buffer.concat(Array.from({ length: height }, () => row))
  return Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4e, 0x47, 0x0d, 0x0a, 0x1a, 0x0a]),
    chunk('IHDR', header),
    chunk('IDAT', deflateSync(raw)),
    chunk('IEND', Buffer.alloc(0)),
  ])
}

/** Uploads a picture to the team's asset store, as the editor's dropzone does. */
export async function uploadImage(
  as: AuthResponse,
  teamSlug: string,
  name: string,
  png: Buffer
): Promise<AssetDto> {
  return withApi(as, async (api) =>
    expectOk<AssetDto>(
      await api.post(`/api/teams/${teamSlug}/assets?assetType=IMAGE`, {
        multipart: { file: { name, mimeType: 'image/png', buffer: png } },
      })
    )
  )
}

export interface NewAd extends Partial<Omit<AdRequest, 'media'>> {
  name: string
  /** Markdown body; the image directives are appended to it. */
  body?: string
  images?: AssetDto[]
}

/** A published sale ad, unless `ad` says otherwise. */
export async function newAd(as: AuthResponse, teamSlug: string, ad: NewAd): Promise<AdDto> {
  const { body = '', images = [], ...rest } = ad
  const markdown = [body, ...images.map((image) => `::asset{id="${image.id}"}`)]
    .filter((part) => part.length > 0)
    .join('\n\n')
  const request: AdRequest = {
    status: 'PUBLISHED',
    adType: 'SALE',
    ...rest,
    media: { markdown, assets: { images, attachments: [] } },
  }
  return apiPost<AdDto>(as, `/api/teams/${teamSlug}/classifieds`, request)
}

/** The ad as `reader` reads it through the API. */
export const getAd = (reader: AuthResponse, teamSlug: string, slug: string) =>
  apiGet<AdDto>(reader, `/api/teams/${teamSlug}/classifieds/${slug}`)
