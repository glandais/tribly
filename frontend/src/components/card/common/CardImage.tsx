import { Image, Box, Center } from '@mantine/core'
import { IconBike, IconArticle, IconRoute, IconUsersGroup, IconTag } from '@tabler/icons-react'
import type { MediaDto } from '@/api/dto'

type CardType = 'RIDE' | 'POST' | 'TRIP' | 'TEAM' | 'AD'

interface CardImageProps {
  media: MediaDto
  alt: string
  height?: number
  type?: CardType
  /**
   * Server-computed URL template of the picture a card header shows, `{size}` token included.
   * Present whatever the list `view`, so it is read before `media.assets`, which a compact row
   * does not carry.
   */
  thumbnailUrl?: string
}

const typeIcons: Record<CardType, typeof IconBike> = {
  RIDE: IconBike,
  POST: IconArticle,
  TRIP: IconRoute,
  TEAM: IconUsersGroup,
  AD: IconTag,
}

const typeGradients: Record<CardType, string> = {
  RIDE: 'linear-gradient(135deg, var(--mantine-color-blue-6) 0%, var(--mantine-color-cyan-5) 100%)',
  POST: 'linear-gradient(135deg, var(--mantine-color-grape-6) 0%, var(--mantine-color-pink-5) 100%)',
  TRIP: 'linear-gradient(135deg, var(--mantine-color-teal-6) 0%, var(--mantine-color-green-5) 100%)',
  TEAM: 'linear-gradient(135deg, var(--mantine-color-violet-6) 0%, var(--mantine-color-indigo-5) 100%)',
  AD: 'linear-gradient(135deg, var(--mantine-color-orange-5) 0%, var(--mantine-color-yellow-4) 100%)',
}

export function CardImage({ media, alt, height = 160, type, thumbnailUrl }: CardImageProps) {
  const { assets } = media

  // Priority 1: the compact-safe thumbnail, else the first image from the asset inventory
  // (actual photos). Both are `{size}`-templated; ×2 for HiDPI.
  const imageUrl = thumbnailUrl ?? assets.images?.[0]?.imageUrl
  if (imageUrl) {
    return (
      <Image
        src={imageUrl.replace('{size}', String(height * 2))}
        alt={alt}
        h={height}
        fit="cover"
        loading="lazy"
      />
    )
  }

  // Priority 2: Gradient background with type icon
  // Logos are shown inline with titles via EntityLogo, not as headers
  // Route thumbnails are shown via RouteThumbnail component
  if (type) {
    const Icon = typeIcons[type]
    const gradient = typeGradients[type]

    return (
      <Box
        h={height}
        style={{
          background: gradient,
        }}
      >
        <Center h="100%">
          <Icon size={48} color="white" opacity={0.8} />
        </Center>
      </Box>
    )
  }

  // No fallback available - don't render anything
  return null
}
