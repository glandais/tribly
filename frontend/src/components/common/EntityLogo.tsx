import { Image } from '@mantine/core'
import { AssetDto } from '@/api/dto'

// Size configurations for the logo
const mantineSizes = {
  xs: 24,
  sm: 32,
  md: 40,
  lg: 48,
  xl: 64,
} as const

export interface EntityLogoProps {
  logo?: AssetDto
  alt?: string
  size?: keyof typeof mantineSizes
}

/**
 * EntityLogo component - displays entity logo without fallback.
 * Use for rides, posts, routes - entities that don't need a placeholder.
 * Returns null if no logo is provided.
 */
export function EntityLogo({ logo, alt = 'Logo', size = 'md' }: EntityLogoProps) {
  if (!logo?.imageUrl) {
    return null
  }

  const pixelSize = mantineSizes[size]
  const url = logo.imageUrl?.replace('{size}', String(pixelSize))

  return <Image src={url} alt={alt} w={pixelSize} h={pixelSize} fit="cover" radius="md" />
}
