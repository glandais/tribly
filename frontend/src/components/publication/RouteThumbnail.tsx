import { Image, Box } from '@mantine/core'
import { IconRoute } from '@tabler/icons-react'

interface RouteThumbnailProps {
  thumbnailUrl?: string
  size?: 'sm' | 'md'
}

const sizes = {
  sm: 80,
  md: 120,
}

export function RouteThumbnail({ thumbnailUrl, size = 'sm' }: RouteThumbnailProps) {
  if (!thumbnailUrl) {
    return null
  }

  const pixelSize = sizes[size]
  // Request 2x size for high-DPI displays
  const requestSize = pixelSize * 2
  const imageUrl = thumbnailUrl.replace('{size}', String(requestSize))

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
export function RouteThumbnailPlaceholder({ size = 'sm' }: { size?: 'sm' | 'md' }) {
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
