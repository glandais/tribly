// Directive descriptor exports a config object referencing an internal component - intentional pattern
/* eslint-disable react-refresh/only-export-components */
import { useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Group, Text, Box, Button, Image, useComputedColorScheme } from '@mantine/core'
import type { DirectiveDescriptor, DirectiveEditorProps } from '@mdxeditor/editor'
import type { LeafDirective } from 'mdast-util-directive'
import {
  IMAGE_SIZES,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
  createImageMap,
  resolveAssetUrl,
  getImageSizeStyle,
} from '../../../lib/assetMarkdown'
import { getOverlayBg } from '@/lib/colors'
import { IconPhoto } from '@tabler/icons-react'
import { useAssetImages } from './AssetImagesContext'

// Size labels for the UI

const SIZE_LABELS: Record<ImageSize, string> = {
  icon: 'XS',
  thumbnail: 'S',
  medium: 'M',
  full: 'L',
}

// ============================================================================
// Custom Asset Directive Editor
// ============================================================================

interface AssetDirectiveAttributes {
  id?: string
  size?: string
  alt?: string
}

function AssetDirectiveEditor({
  mdastNode,
  lexicalNode,
  parentEditor,
}: DirectiveEditorProps<LeafDirective>) {
  const { t } = useTranslation()
  const colorScheme = useComputedColorScheme('light')
  const images = useAssetImages()

  const attributes = (mdastNode.attributes ?? {}) as AssetDirectiveAttributes
  const assetId = attributes.id ?? ''
  const currentSize = (attributes.size as ImageSize) ?? DEFAULT_IMAGE_SIZE
  const altText = attributes.alt ?? ''

  // Resolve asset URL
  const url = useMemo(() => {
    if (!assetId) return undefined
    const imageMap = createImageMap(images)
    return resolveAssetUrl(assetId, imageMap)
  }, [assetId, images])

  const sizeStyle = getImageSizeStyle(currentSize)

  // Update the directive attributes
  const updateSize = (newSize: ImageSize) => {
    parentEditor.update(() => {
      // Build attributes, omitting size if it's the default
      const newAttributes: Record<string, string> = { id: assetId }
      if (altText) {
        newAttributes.alt = altText
      }
      if (newSize !== DEFAULT_IMAGE_SIZE) {
        newAttributes.size = newSize
      }
      lexicalNode.setMdastNode({
        ...mdastNode,
        attributes: newAttributes,
      })
    })
  }

  // Missing image (asset not found in images array)
  if (!url) {
    return (
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
    )
  }

  return (
    <Box className="asset-directive-container">
      {/* Image */}
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
  )
}

// ============================================================================
// Directive Descriptor
// ============================================================================

export const AssetDirectiveDescriptor: DirectiveDescriptor<LeafDirective> = {
  name: 'asset',
  type: 'leafDirective',
  testNode(node) {
    return node.name === 'asset'
  },
  attributes: ['id', 'size', 'alt'],
  hasChildren: false,
  Editor: AssetDirectiveEditor,
}
