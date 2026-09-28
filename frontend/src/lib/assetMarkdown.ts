import type { AssetDto } from '@/api/dto'

/**
 * Centralized utilities for handling asset references in markdown.
 * Asset directive format: ::asset{id="abc123" size="medium" alt="description"}
 */

// ============================================================================
// Image Size Types and Constants
// ============================================================================

/** Available image sizes */
export type ImageSize = 'icon' | 'thumbnail' | 'medium' | 'full'

/** List of all image sizes for iteration */
export const IMAGE_SIZES: ImageSize[] = ['icon', 'thumbnail', 'medium', 'full']

/** Default size for new uploads */
export const DEFAULT_IMAGE_SIZE: ImageSize = 'medium'

/** Mantine style props for each image size */
export interface ImageSizeStyle {
  w?: number | string
  h?: number | string
  maw?: number | string
  fit?: 'cover' | 'contain' | 'fill' | 'none' | 'scale-down'
}

const IMAGE_SIZE_STYLES: Record<ImageSize, ImageSizeStyle> = {
  icon: { w: 32, h: 32, fit: 'cover' },
  thumbnail: { w: 96, h: 'auto' },
  medium: { maw: 448, h: 'auto' },
  full: { maw: '100%', h: 'auto' },
}

/** Width in pixels for each image size (used for imgproxy resizing) */
const IMAGE_SIZE_WIDTHS: Record<ImageSize, number> = {
  icon: 32,
  thumbnail: 96,
  medium: 448,
  full: 1920,
}

/**
 * Get the width in pixels for an image size
 */
export function getImageSizeWidth(size?: ImageSize): number {
  return IMAGE_SIZE_WIDTHS[size ?? DEFAULT_IMAGE_SIZE]
}

/**
 * Get Mantine style props for an image size
 */
export function getImageSizeStyle(size?: ImageSize): ImageSizeStyle {
  return IMAGE_SIZE_STYLES[size ?? DEFAULT_IMAGE_SIZE]
}

// ============================================================================
// Asset URL Resolution
// ============================================================================

/**
 * Resolve an asset ID to a sized image URL.
 * Uses imageUrl with size substitution if available, otherwise falls back to raw url.
 */
export function resolveAssetImageUrl(
  assetId: string,
  images: AssetDto[],
  size?: ImageSize
): string | undefined {
  const asset = images.find((img) => img.id === assetId)
  if (!asset) return undefined

  if (asset.imageUrl) {
    const sizeWidth = getImageSizeWidth(size)
    return asset.imageUrl.replace('{size}', String(sizeWidth))
  }
  return asset.url
}

// ============================================================================
// Directive Format Utilities (::asset{} syntax)
// ============================================================================

/** Regex to match asset directive: ::asset{id="..." size="..." alt="..."} */
export const ASSET_DIRECTIVE_REGEX = /::asset\{([^}]+)\}/g

/**
 * Create directive syntax string for an asset
 * e.g., ("abc123", "My image", "medium") -> '::asset{id="abc123" alt="My image"}'
 */
export function createAssetDirective(
  assetId: string,
  altText: string = '',
  size?: ImageSize
): string {
  const parts: string[] = [`id="${assetId}"`]

  // Only include size if it's not the default
  if (size && size !== DEFAULT_IMAGE_SIZE) {
    parts.push(`size="${size}"`)
  }

  const alt = directiveValue(altText)
  if (alt) {
    parts.push(`alt="${alt}"`)
  }

  return `::asset{${parts.join(' ')}}`
}

/**
 * Make free text safe as a quoted directive value.
 *
 * No reader of `::asset{}` decodes escapes: remark-directive, the regexes here, in the mobile app
 * and in the backend all stop a value at the first `"` and the directive at the first `}`. A
 * backslash escape would only leave a stray `\` and cut the alt text short, so the characters that
 * would end the value or the directive are swapped for look-alikes instead, and line breaks (a leaf
 * directive fits on one line) become spaces.
 */
export function directiveValue(text: string): string {
  return text
    .replace(/"/g, "'")
    .replace(/\{/g, '(')
    .replace(/\}/g, ')')
    .replace(/\s*[\r\n]+\s*/g, ' ')
    .trim()
}

/**
 * Parse directive attributes from a string like 'id="abc" size="medium" alt="text"'
 */
export function parseDirectiveAttributes(attributeString: string): Record<string, string> {
  const attrs: Record<string, string> = {}
  const attrRegex = /(\w+)="([^"]*)"/g
  let match

  while ((match = attrRegex.exec(attributeString)) !== null) {
    attrs[match[1]] = match[2]
  }

  return attrs
}

/**
 * Parse asset directive to extract id, size, and alt
 */
export function parseAssetDirective(attributes: Record<string, string>): {
  assetId: string
  size?: ImageSize
  alt?: string
} | null {
  const id = attributes.id
  if (!id) return null

  const sizeAttr = attributes.size
  const size =
    sizeAttr && IMAGE_SIZES.includes(sizeAttr as ImageSize) ? (sizeAttr as ImageSize) : undefined

  return {
    assetId: id,
    size,
    alt: attributes.alt,
  }
}
