import type { Root } from 'mdast'
import type { Node } from 'unist'
import { visit } from 'unist-util-visit'
import { IMAGE_SIZES, type ImageSize, DEFAULT_IMAGE_SIZE } from './assetMarkdown'

interface DirectiveNode extends Node {
  name: string
  attributes?: Record<string, string>
  data?: {
    hName?: string
    hProperties?: Record<string, string>
  }
}

function isDirectiveNode(node: Node): node is DirectiveNode {
  return (
    (node.type === 'leafDirective' || node.type === 'containerDirective') &&
    'name' in node
  )
}

/**
 * Remark plugin to transform ::asset{} directives into renderable nodes.
 *
 * Input (in markdown):
 *   ::asset{id="abc123" size="medium" alt="My image"}
 *
 * Output (for react-markdown components):
 *   <asset-image id="abc123" size="medium" alt="My image" />
 */
export function remarkAssetDirective() {
  return (tree: Root) => {
    visit(tree, (node: Node) => {
      if (!isDirectiveNode(node)) {
        return
      }

      if (node.name !== 'asset') {
        return
      }

      const attributes = node.attributes ?? {}
      const id = attributes.id
      const sizeAttr = attributes.size
      const alt = attributes.alt ?? ''

      // Validate size attribute
      const size: ImageSize =
        sizeAttr && IMAGE_SIZES.includes(sizeAttr as ImageSize)
          ? (sizeAttr as ImageSize)
          : DEFAULT_IMAGE_SIZE

      // Transform to hast node for react-markdown
      node.data = node.data ?? {}
      node.data.hName = 'asset-image'
      node.data.hProperties = {
        id: id ?? '',
        size,
        alt,
      }
    })
  }
}
