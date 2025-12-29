import { useState, useCallback, useEffect, useRef } from 'react'
import { PhotoIcon, XMarkIcon } from '@heroicons/react/24/outline'
import { useLexicalComposerContext } from '@lexical/react/LexicalComposerContext'
import { useLexicalNodeSelection } from '@lexical/react/useLexicalNodeSelection'
import { mergeRegister } from '@lexical/utils'
import {
  $getNodeByKey,
  $getSelection,
  $isNodeSelection,
  CLICK_COMMAND,
  COMMAND_PRIORITY_LOW,
  KEY_BACKSPACE_COMMAND,
  KEY_DELETE_COMMAND,
  type NodeKey,
} from 'lexical'
import { useImages } from './ImageResolverContext'
import { $isImageNode } from './ImageNode'
import { AssetImage } from '../AssetImage'
import {
  parseAssetSrc,
  IMAGE_SIZES,
  type ImageSize,
  DEFAULT_IMAGE_SIZE,
} from '../../../lib/assetMarkdown'

interface ImageComponentProps {
  src: string
  altText: string
  nodeKey: NodeKey
}

/** Size button labels */
const SIZE_LABELS: Record<ImageSize, string> = {
  icon: 'XS',
  thumbnail: 'S',
  medium: 'M',
  full: 'L',
}

export function ImageComponent({ src, altText, nodeKey }: ImageComponentProps) {
  const [editor] = useLexicalComposerContext()
  const images = useImages()
  const imageRef = useRef<HTMLDivElement>(null)
  const [isSelected, setSelected, clearSelection] = useLexicalNodeSelection(nodeKey)
  const [isHovered, setIsHovered] = useState(false)

  // Parse asset src to get assetId and size
  const parsed = parseAssetSrc(src)
  const assetId = parsed?.assetId
  const currentSize = parsed?.size ?? DEFAULT_IMAGE_SIZE

  // Show controls on hover or selection
  const showControls = isSelected || isHovered

  const onDelete = useCallback(
    (event: KeyboardEvent) => {
      if (isSelected && $isNodeSelection($getSelection())) {
        event.preventDefault()
        const node = $getNodeByKey(nodeKey)
        if ($isImageNode(node)) {
          node.remove()
          return true
        }
      }
      return false
    },
    [isSelected, nodeKey]
  )

  const onClick = useCallback(
    (event: MouseEvent) => {
      if (imageRef.current && imageRef.current.contains(event.target as Node)) {
        if (!event.shiftKey) {
          clearSelection()
        }
        setSelected(true)
        return true
      }
      return false
    },
    [clearSelection, setSelected]
  )

  const onDeleteClick = useCallback(() => {
    editor.update(() => {
      const node = $getNodeByKey(nodeKey)
      if ($isImageNode(node)) {
        node.remove()
      }
    })
  }, [editor, nodeKey])

  const onSizeChange = useCallback(
    (size: ImageSize) => {
      editor.update(() => {
        const node = $getNodeByKey(nodeKey)
        if ($isImageNode(node)) {
          node.setSize(size)
        }
      })
    },
    [editor, nodeKey]
  )

  useEffect(() => {
    return mergeRegister(
      editor.registerCommand(CLICK_COMMAND, onClick, COMMAND_PRIORITY_LOW),
      editor.registerCommand(KEY_DELETE_COMMAND, onDelete, COMMAND_PRIORITY_LOW),
      editor.registerCommand(KEY_BACKSPACE_COMMAND, onDelete, COMMAND_PRIORITY_LOW)
    )
  }, [editor, onClick, onDelete])

  // Handle missing asset ID (not an asset src)
  if (!assetId) {
    return (
      <div
        ref={imageRef}
        className={`flex items-center gap-2 px-3 py-2 bg-gray-100 text-gray-500 text-sm rounded-lg my-2 cursor-pointer ${
          isSelected ? 'ring-2 ring-cyan-500' : ''
        }`}
      >
        <PhotoIcon className="w-5 h-5 shrink-0" />
        <span>{altText || 'Invalid image'}</span>
        {isSelected && (
          <button
            type="button"
            onClick={onDeleteClick}
            className="ml-auto p-1 hover:bg-gray-200 rounded"
            title="Delete image"
          >
            <XMarkIcon className="w-4 h-4" />
          </button>
        )}
      </div>
    )
  }

  return (
    <div
      ref={imageRef}
      className="my-4 inline-flex flex-col items-start cursor-pointer"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {/* Image with selection ring */}
      <div className={`${isSelected ? 'ring-2 ring-cyan-500 ring-offset-2 rounded-lg' : ''}`}>
        <AssetImage assetId={assetId} images={images} size={currentSize} altText={altText} />
      </div>

      {/* Controls toolbar - always rendered, visibility toggled to prevent layout shift */}
      <div
        className={`flex items-center gap-2 mt-1 transition-opacity ${
          showControls ? 'opacity-100' : 'opacity-0 pointer-events-none'
        }`}
      >
        {/* Size buttons */}
        <div className="flex items-center bg-gray-800 rounded-lg overflow-hidden">
          {IMAGE_SIZES.map((size) => (
            <button
              key={size}
              type="button"
              onClick={() => onSizeChange(size)}
              className={`px-2 py-1 text-xs font-medium transition-colors ${
                currentSize === size ? 'bg-cyan-500 text-white' : 'text-gray-300 hover:bg-gray-700'
              }`}
              title={size}
            >
              {SIZE_LABELS[size]}
            </button>
          ))}
        </div>

        {/* Delete button */}
        <button
          type="button"
          onClick={onDeleteClick}
          className="p-1.5 bg-gray-800 hover:bg-red-600 text-gray-300 hover:text-white rounded-lg transition-colors"
          title="Delete image"
        >
          <XMarkIcon className="w-4 h-4" />
        </button>
      </div>
    </div>
  )
}
