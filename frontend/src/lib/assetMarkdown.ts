import type { AssetDto } from '../api/api'

/**
 * Centralized utilities for handling asset references in markdown.
 * Asset format: ![alt text](asset:id) or ![alt text](asset:id?size=medium)
 */

/** Asset protocol prefix */
export const ASSET_PROTOCOL = 'asset:'

/** Available image sizes */
export type ImageSize = 'icon' | 'thumbnail' | 'medium' | 'full'

/** List of all image sizes for iteration */
export const IMAGE_SIZES: ImageSize[] = ['icon', 'thumbnail', 'medium', 'full']

/** Default size for new uploads */
export const DEFAULT_IMAGE_SIZE: ImageSize = 'medium'

/** CSS classes for each image size */
const IMAGE_SIZE_CLASSES: Record<ImageSize, string> = {
  icon: 'w-8 h-8 object-cover',
  thumbnail: 'w-24 h-auto',
  medium: 'max-w-md h-auto',
  full: 'max-w-full h-auto',
}

/** Regex to match asset image markdown: ![alt](asset:id) or ![alt](asset:id?size=X) */
export const ASSET_IMAGE_REGEX = /!\[([^\]]*)\]\(asset:([a-z0-9]+)(?:\?size=([a-z]+))?\)/g

/** Regex to match asset image markdown at line start (for Lexical ElementTransformer) */
export const ASSET_IMAGE_LINE_REGEX = /^!\[([^\]]*)\]\(asset:([a-z0-9]+)(?:\?size=([a-z]+))?\)\s*$/

/**
 * Check if a src string is an asset reference
 */
export function isAssetSrc(src: string): boolean {
  return src.startsWith(ASSET_PROTOCOL)
}

/**
 * Parse an asset src string to extract ID and optional size
 * e.g., "asset:abc123?size=medium" → { assetId: "abc123", size: "medium" }
 * Returns null if not an asset src
 */
export function parseAssetSrc(src: string): { assetId: string; size?: ImageSize } | null {
  if (!isAssetSrc(src)) return null

  const withoutProtocol = src.substring(ASSET_PROTOCOL.length)
  const [assetId, queryString] = withoutProtocol.split('?')

  let size: ImageSize | undefined
  if (queryString) {
    const params = new URLSearchParams(queryString)
    const sizeParam = params.get('size')
    if (sizeParam && IMAGE_SIZES.includes(sizeParam as ImageSize)) {
      size = sizeParam as ImageSize
    }
  }

  return { assetId, size }
}

/**
 * Extract just the asset ID from an asset src string (legacy compatibility)
 * e.g., "asset:abc123?size=medium" → "abc123"
 * Returns null if not an asset src
 */
export function parseAssetId(src: string): string | null {
  const parsed = parseAssetSrc(src)
  return parsed?.assetId ?? null
}

/**
 * Create an asset src string from an asset ID and optional size
 * e.g., ("abc123", "medium") → "asset:abc123?size=medium"
 */
export function createAssetSrc(assetId: string, size?: ImageSize): string {
  const base = `${ASSET_PROTOCOL}${assetId}`
  if (size && size !== DEFAULT_IMAGE_SIZE) {
    return `${base}?size=${size}`
  }
  return base
}

/**
 * Create markdown image syntax for an asset
 */
export function createAssetMarkdown(assetId: string, altText: string, size?: ImageSize): string {
  return `![${altText}](${createAssetSrc(assetId, size)})`
}

/**
 * Get CSS class string for an image size
 */
export function getImageSizeClass(size?: ImageSize): string {
  return IMAGE_SIZE_CLASSES[size ?? DEFAULT_IMAGE_SIZE]
}

/**
 * Create a lookup map from asset ID to URL for O(1) resolution
 */
export function createImageMap(images: AssetDto[]): Map<string, string> {
  return new Map(images.map((img) => [img.id, img.url]))
}

/**
 * Resolve an asset ID to its URL using an image map
 * Returns undefined if not found
 */
export function resolveAssetUrl(
  assetId: string,
  imageMap: Map<string, string | undefined>
): string | undefined {
  return imageMap.get(assetId)
}
