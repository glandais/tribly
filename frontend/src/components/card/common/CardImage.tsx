import { Image, Box, Center } from '@mantine/core'
import { useComputedColorScheme } from '@mantine/core'
import { IconBike, IconArticle, IconRoute } from '@tabler/icons-react'
import type { MediaDto } from '@/api/dto'

type PublicationType = 'RIDE' | 'POST' | 'TRIP'

interface CardImageProps {
  media: MediaDto
  alt: string
  height?: number
  type?: PublicationType
}

const typeIcons: Record<PublicationType, typeof IconBike> = {
  RIDE: IconBike,
  POST: IconArticle,
  TRIP: IconRoute,
}

const typeGradients: Record<PublicationType, string> = {
  RIDE: 'linear-gradient(135deg, var(--mantine-color-blue-6) 0%, var(--mantine-color-cyan-5) 100%)',
  POST: 'linear-gradient(135deg, var(--mantine-color-grape-6) 0%, var(--mantine-color-pink-5) 100%)',
  TRIP: 'linear-gradient(135deg, var(--mantine-color-teal-6) 0%, var(--mantine-color-green-5) 100%)',
}

export function CardImage({ media, alt, height = 160, type }: CardImageProps) {
  const { assets } = media
  const colorScheme = useComputedColorScheme('light')

  // Priority 1: First image from images array
  const firstImage = assets.images?.[0]
  if (firstImage?.imageUrl) {
    return (
      <Image
        src={firstImage.imageUrl.replace('{size}', String(height * 2))}
        alt={alt}
        h={height}
        fit="cover"
      />
    )
  }

  // Priority 2: Thumbnail (color scheme aware)
  const thumbnail = colorScheme === 'dark' ? assets.thumbnailDark : assets.thumbnailLight
  if (thumbnail?.imageUrl) {
    return (
      <Image
        src={thumbnail.imageUrl.replace('{size}', String(height * 2))}
        alt={alt}
        h={height}
        fit="contain"
      />
    )
  }

  // Priority 3: Logo centered on muted background
  if (assets.logo?.imageUrl) {
    return (
      <Box
        h={height}
        style={{
          backgroundColor: 'var(--mantine-color-gray-1)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <Image
          src={assets.logo.imageUrl?.replace('{size}', String(160))}
          alt={alt}
          w={80}
          h={80}
          fit="contain"
          radius="md"
        />
      </Box>
    )
  }

  // Priority 4: Gradient background with type icon
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
