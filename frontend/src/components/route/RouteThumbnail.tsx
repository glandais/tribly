import { Image, Box } from '@mantine/core'
import { useComputedColorScheme } from '@mantine/core'
import { IconRoute } from '@tabler/icons-react'

interface RouteThumbnailProps {
  thumbnailLightUrl?: string
  thumbnailDarkUrl?: string
  size?: 'sm' | 'md' | 'lg'
}

const sizes = {
  sm: 80,
  md: 120,
  lg: 160,
}

export function RouteThumbnail({
  thumbnailLightUrl,
  thumbnailDarkUrl,
  size = 'sm',
}: RouteThumbnailProps) {
  const colorScheme = useComputedColorScheme('light')

  // Pick the right thumbnail based on color scheme, with fallback to legacy
  const selectedUrl = colorScheme === 'dark' ? thumbnailDarkUrl : thumbnailLightUrl

  if (!selectedUrl) {
    return null
  }

  const pixelSize = sizes[size]
  // Request 2x size for high-DPI displays
  const requestSize = pixelSize * 2
  const imageUrl = selectedUrl.replace('{size}', String(requestSize))

  return (
    <Box
      style={{
        width: pixelSize,
        height: pixelSize,
        borderRadius: 'var(--mantine-radius-md)',
        overflow: 'hidden',
        flexShrink: 0,
        border: '1px solid var(--mantine-color-gray-3)',
      }}
    >
      <Image
        src={imageUrl}
        alt="Route preview"
        w={pixelSize}
        h={pixelSize}
        fit="cover"
        fallbackSrc={undefined}
      />
    </Box>
  )
}

// Placeholder component for when route data is being loaded
export function RouteThumbnailPlaceholder({ size = 'sm' }: { size?: 'sm' | 'md' | 'lg' }) {
  const pixelSize = sizes[size]

  return (
    <Box
      style={{
        width: pixelSize,
        height: pixelSize,
        borderRadius: 'var(--mantine-radius-md)',
        backgroundColor: 'var(--mantine-color-gray-1)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        flexShrink: 0,
      }}
    >
      <IconRoute size={24} color="var(--mantine-color-gray-5)" />
    </Box>
  )
}
