import { useState, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Text, Box, Center, Image } from '@mantine/core'
import { IconPhoto } from '@tabler/icons-react'
import type { AssetDto } from '@/api/dto'
import {
  getImageSizeStyle,
  resolveAssetImageUrl,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
} from '../../lib/assetMarkdown'
import { LoadingSpinner } from './LoadingSpinner'

export interface AssetImageProps {
  assetId: string
  images: AssetDto[]
  size?: ImageSize
  altText?: string
}

export function AssetImage({
  assetId,
  images,
  size = DEFAULT_IMAGE_SIZE,
  altText = '',
}: AssetImageProps) {
  const { t } = useTranslation()
  const [isLoading, setIsLoading] = useState(true)
  const [hasError, setHasError] = useState(false)

  const url = useMemo(() => resolveAssetImageUrl(assetId, images, size), [assetId, images, size])

  const sizeStyle = getImageSizeStyle(size)

  // Missing image (asset not found in images array)
  if (!url) {
    return (
      <Group
        gap="xs"
        p="xs"
        bg="var(--mantine-color-default-hover)"
        style={{ borderRadius: 'var(--mantine-radius-sm)', display: 'inline-flex' }}
      >
        <IconPhoto size={16} color="var(--mantine-color-dimmed)" />
        <Text size="sm" c="dimmed">
          {altText || t('images.notFound')}
        </Text>
      </Group>
    )
  }

  // Error loading image
  if (hasError) {
    return (
      <Group
        gap="xs"
        p="xs"
        bg="var(--mantine-color-red-light)"
        style={{ borderRadius: 'var(--mantine-radius-sm)', display: 'inline-flex' }}
      >
        <IconPhoto size={16} color="var(--mantine-color-red-filled)" />
        <Text size="sm" c="red">
          {t('error.loading')}
        </Text>
      </Group>
    )
  }

  return (
    <Box display="inline-block">
      {isLoading && (
        <Center
          p="md"
          bg="var(--mantine-color-default-hover)"
          style={{ borderRadius: 'var(--mantine-radius-md)' }}
        >
          <LoadingSpinner size="sm" />
        </Center>
      )}
      <Image
        src={url}
        alt={altText}
        w={sizeStyle.w}
        h={sizeStyle.h}
        maw={sizeStyle.maw}
        fit={sizeStyle.fit}
        radius="md"
        style={{ display: isLoading ? 'none' : 'block' }}
        onLoad={() => setIsLoading(false)}
        onError={() => {
          setIsLoading(false)
          setHasError(true)
        }}
      />
    </Box>
  )
}
