import { createContext, useContext, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import type { DirectiveDescriptor, DirectiveEditorProps } from '@mdxeditor/editor'
import type { LeafDirective } from 'mdast-util-directive'
import type { AssetDto } from '../../../api/api'
import {
  IMAGE_SIZES,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
  createImageMap,
  resolveAssetUrl,
  getImageSizeClass,
} from '../../../lib/assetMarkdown'
import { PhotoIcon } from '@heroicons/react/24/outline'

// ============================================================================
// Context for providing images to directive editors
// ============================================================================

interface AssetImagesContextValue {
  images: AssetDto[]
}

const AssetImagesContext = createContext<AssetImagesContextValue>({ images: [] })

export function AssetImagesProvider({
  images,
  children,
}: {
  images: AssetDto[]
  children: React.ReactNode
}) {
  const value = useMemo(() => ({ images }), [images])
  return <AssetImagesContext.Provider value={value}>{children}</AssetImagesContext.Provider>
}

export function useAssetImages(): AssetDto[] {
  return useContext(AssetImagesContext).images
}

// ============================================================================
// Size labels for the UI
// ============================================================================

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
  const { t } = useTranslation('common')
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

  const sizeClass = getImageSizeClass(currentSize)

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
      <span className="inline-flex items-center gap-1 px-2 py-1 bg-gray-100 text-gray-500 text-sm rounded">
        <PhotoIcon className="w-4 h-4 shrink-0" />
        <span>{altText || t('images.notFound')}</span>
      </span>
    )
  }

  return (
    <div className="relative inline-block group my-2">
      {/* Image */}
      <img src={url} alt={altText} className={`${sizeClass} rounded-lg`} draggable={false} />

      {/* Size controls - visible on hover */}
      <div className="absolute top-2 left-2 opacity-0 group-hover:opacity-100 transition-opacity">
        <div className="flex gap-1 bg-white/90 backdrop-blur-sm rounded-lg shadow-lg p-1">
          {IMAGE_SIZES.map((size) => (
            <button
              key={size}
              type="button"
              onClick={() => updateSize(size)}
              className={`px-2 py-1 text-xs font-medium rounded transition-colors ${
                currentSize === size
                  ? 'bg-cyan-500 text-white'
                  : 'bg-gray-100 text-gray-700 hover:bg-gray-200'
              }`}
              title={t(
                `editor.imageSize.${size satisfies 'icon' | 'thumbnail' | 'medium' | 'full'}`
              )}
            >
              {SIZE_LABELS[size]}
            </button>
          ))}
        </div>
      </div>
    </div>
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
