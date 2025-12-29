import { AssetDto } from '../../api/api'

// Size configurations for the logo
const sizeClasses = {
  xs: 'h-6 w-6',
  sm: 'h-8 w-8',
  md: 'h-10 w-10',
  lg: 'h-12 w-12',
  xl: 'h-16 w-16',
} as const

export interface EntityLogoProps {
  logo?: AssetDto
  alt?: string
  size?: keyof typeof sizeClasses
  className?: string
}

/**
 * EntityLogo component - displays entity logo without fallback.
 * Use for rides, posts, routes - entities that don't need a placeholder.
 * Returns null if no logo is provided.
 */
export function EntityLogo({ logo, alt = 'Logo', size = 'md', className = '' }: EntityLogoProps) {
  if (!logo?.url) {
    return null
  }

  const sizeClass = sizeClasses[size]

  return (
    <img src={logo.url} alt={alt} className={`${sizeClass} object-cover rounded-lg ${className}`} />
  )
}
