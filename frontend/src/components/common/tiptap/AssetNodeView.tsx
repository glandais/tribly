import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { NodeViewWrapper, type NodeViewProps } from '@tiptap/react'
import { Group, Text, Box, Button, Image, useComputedColorScheme } from '@mantine/core'
import { IconPhoto } from '@tabler/icons-react'
import {
  IMAGE_SIZES,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
  getImageSizeStyle,
  resolveAssetImageUrl,
} from '@/lib/assetMarkdown'
import { getOverlayBg } from '@/lib/colors'
import { useAssetImages } from './AssetImagesContext'

const SIZE_LABELS: Record<ImageSize, string> = {
  icon: 'XS',
  thumbnail: 'S',
  medium: 'M',
  full: 'L',
}

export function AssetNodeView({ node, updateAttributes, selected }: NodeViewProps) {
  const { t } = useTranslation()
  const colorScheme = useComputedColorScheme('light')
  const images = useAssetImages()

  const assetId = node.attrs.id as string
  const currentSize = (node.attrs.size as ImageSize) || DEFAULT_IMAGE_SIZE
  const altText = (node.attrs.alt as string) || ''

  const url = useMemo(
    () => (assetId ? resolveAssetImageUrl(assetId, images, currentSize) : undefined),
    [assetId, images, currentSize]
  )

  const sizeStyle = getImageSizeStyle(currentSize)

  const updateSize = (newSize: ImageSize) => {
    updateAttributes({ size: newSize })
  }

  // Missing image (asset not found in images array)
  if (!url) {
    return (
      <NodeViewWrapper className="asset-node-wrapper">
        <Group
          gap="xs"
          px="xs"
          py={4}
          bg="var(--mantine-color-default-hover)"
          style={{ borderRadius: 'var(--mantine-radius-sm)', display: 'inline-flex' }}
        >
          <IconPhoto size={16} style={{ flexShrink: 0 }} />
          <Text size="sm" c="dimmed">
            {altText || t('images.notFound')}
          </Text>
        </Group>
      </NodeViewWrapper>
    )
  }

  return (
    <NodeViewWrapper className="asset-node-wrapper">
      <Box
        className="asset-directive-container"
        style={{
          outline: selected ? '2px solid var(--mantine-primary-color-filled)' : 'none',
          borderRadius: 'var(--mantine-radius-md)',
        }}
      >
        <Image
          src={url}
          alt={altText}
          w={sizeStyle.w}
          h={sizeStyle.h}
          maw={sizeStyle.maw}
          fit={sizeStyle.fit}
          radius="md"
          style={{ pointerEvents: 'none' }}
        />

        {/* Size controls - visible on hover */}
        <Box pos="absolute" top={8} left={8} className="asset-controls">
          <Group
            gap={4}
            p={4}
            bg={getOverlayBg(colorScheme)}
            style={{
              backdropFilter: 'blur(4px)',
              borderRadius: 'var(--mantine-radius-md)',
              boxShadow: 'var(--mantine-shadow-lg)',
            }}
          >
            {IMAGE_SIZES.map((size) => (
              <Button
                key={size}
                size="compact-xs"
                variant={currentSize === size ? 'filled' : 'light'}
                color={currentSize === size ? 'cyan' : 'gray'}
                onClick={() => updateSize(size)}
                title={t(
                  `editor.imageSize.${size satisfies 'icon' | 'thumbnail' | 'medium' | 'full'}`
                )}
              >
                {SIZE_LABELS[size]}
              </Button>
            ))}
          </Group>
        </Box>
      </Box>
    </NodeViewWrapper>
  )
}
