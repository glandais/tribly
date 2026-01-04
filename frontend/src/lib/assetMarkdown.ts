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

/** CSS classes for each image size */
const IMAGE_SIZE_CLASSES: Record<ImageSize, string> = {
  icon: 'w-8 h-8 object-cover',
  thumbnail: 'w-24 h-auto',
  medium: 'max-w-md h-auto',
  full: 'max-w-full h-auto',
}

/**
 * Get CSS class string for an image size
 */
export function getImageSizeClass(size?: ImageSize): string {
  return IMAGE_SIZE_CLASSES[size ?? DEFAULT_IMAGE_SIZE]
}

// ============================================================================
// Image Map Utilities
// ============================================================================

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

// ============================================================================
// Directive Format Utilities (::asset{} syntax)
// ============================================================================

/** Regex to match asset directive: ::asset{id="..." size="..." alt="..."} */
export const ASSET_DIRECTIVE_REGEX = /::asset\{([^}]+)\}/g

/**
 * Create directive syntax string for an asset
 * e.g., ("abc123", "My image", "medium") → '::asset{id="abc123" alt="My image"}'
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

  // Include alt text if present
  if (altText) {
    // Escape double quotes in alt text
    const escapedAlt = altText.replace(/"/g, '\\"')
    parts.push(`alt="${escapedAlt}"`)
  }

  return `::asset{${parts.join(' ')}}`
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
