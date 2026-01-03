import { useState, useMemo } from 'react'
import { PhotoIcon } from '@heroicons/react/24/outline'
import { useTranslation } from 'react-i18next'
import type { AssetDto } from '../../api/api'
import {
  createImageMap,
  resolveAssetUrl,
  getImageSizeClass,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
} from '../../lib/assetMarkdown'
import { LoadingSpinner } from './LoadingSpinner'

export interface AssetImageProps {
  assetId: string
  images: AssetDto[]
  size?: ImageSize
  altText?: string
  className?: string
}

/**
 * Shared component for rendering asset images with size support.
 * Handles loading, error, and missing image states.
 * Used by both MarkdownEditor (ImageComponent) and MarkdownDisplay.
 */
export function AssetImage({
  assetId,
  images,
  size = DEFAULT_IMAGE_SIZE,
  altText = '',
  className = '',
}: AssetImageProps) {
  const { t } = useTranslation('common')
  const [isLoading, setIsLoading] = useState(true)
  const [hasError, setHasError] = useState(false)

  const url = useMemo(() => {
    const imageMap = createImageMap(images)
    return resolveAssetUrl(assetId, imageMap)
  }, [assetId, images])

  const sizeClass = getImageSizeClass(size)

  // Missing image (asset not found in images array)
  if (!url) {
    return (
      <span
        className={`inline-flex items-center gap-1 px-2 py-1 bg-gray-100 text-gray-500 text-sm rounded ${className}`}
      >
        <PhotoIcon className="w-4 h-4 shrink-0" />
        <span>{altText || t('images.notFound')}</span>
      </span>
    )
  }

  // Error loading image
  if (hasError) {
    return (
      <span
        className={`inline-flex items-center gap-1 px-2 py-1 bg-red-50 text-red-500 text-sm rounded ${className}`}
      >
        <PhotoIcon className="w-4 h-4 shrink-0" />
        <span>{t('error.loading')}</span>
      </span>
    )
  }

  return (
    <span className={`inline-block ${className}`}>
      {isLoading && (
        <span className="flex items-center justify-center p-4 bg-gray-100 rounded-lg">
          <LoadingSpinner size="sm" />
        </span>
      )}
      <img
        src={url}
        alt={altText}
        className={`${sizeClass} rounded-lg ${isLoading ? 'hidden' : 'block'}`}
        onLoad={() => setIsLoading(false)}
        onError={() => {
          setIsLoading(false)
          setHasError(true)
        }}
        draggable={false}
      />
    </span>
  )
}
