import type { ElementTransformer } from '@lexical/markdown'
import { $createImageNode, $isImageNode, ImageNode } from './ImageNode'
import {
  ASSET_IMAGE_LINE_REGEX,
  createAssetSrc,
  IMAGE_SIZES,
  type ImageSize,
} from '../../../lib/assetMarkdown'

export const IMAGE_TRANSFORMER: ElementTransformer = {
  dependencies: [ImageNode],
  type: 'element',

  // For importing markdown (when editor loads)
  // Uses line regex that matches asset: protocol URLs with optional ?size=X
  regExp: ASSET_IMAGE_LINE_REGEX,

  // Convert matched markdown line to ImageNode
  // Match groups: [full, altText, assetId, size?]
  replace: (parentNode, _children, match) => {
    const [, altText, assetId, sizeStr] = match
    const size =
      sizeStr && IMAGE_SIZES.includes(sizeStr as ImageSize) ? (sizeStr as ImageSize) : undefined
    const imageNode = $createImageNode(createAssetSrc(assetId, size), altText)
    parentNode.replace(imageNode)
  },

  // Convert ImageNode back to markdown text
  // src already contains the full asset URL with size param
  export: (node, _exportChildren) => {
    if (!$isImageNode(node)) {
      return null
    }
    const altText = node.getAltText()
    const src = node.getSrc()
    return `![${altText}](${src})`
  },
}
