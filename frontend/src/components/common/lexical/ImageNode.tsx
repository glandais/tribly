import type { ReactNode } from 'react'
import {
  DecoratorNode,
  type DOMConversionMap,
  type DOMExportOutput,
  type LexicalNode,
  type NodeKey,
  type SerializedLexicalNode,
  type Spread,
  $applyNodeReplacement,
} from 'lexical'
import { ImageComponent } from './ImageComponent'
import { parseAssetSrc, createAssetSrc, type ImageSize } from '../../../lib/assetMarkdown'

export type SerializedImageNode = Spread<
  {
    src: string
    altText: string
  },
  SerializedLexicalNode
>

export class ImageNode extends DecoratorNode<ReactNode> {
  __src: string
  __altText: string

  static getType(): string {
    return 'image'
  }

  static clone(node: ImageNode): ImageNode {
    return new ImageNode(node.__src, node.__altText, node.__key)
  }

  constructor(src: string, altText: string, key?: NodeKey) {
    super(key)
    this.__src = src
    this.__altText = altText
  }

  getSrc(): string {
    return this.getLatest().__src
  }

  setSrc(src: string): void {
    this.getWritable().__src = src
  }

  getAltText(): string {
    return this.getLatest().__altText
  }

  /**
   * Get the image size from the src query parameter
   */
  getSize(): ImageSize | undefined {
    const parsed = parseAssetSrc(this.getSrc())
    return parsed?.size
  }

  /**
   * Set the image size by updating the src query parameter
   */
  setSize(size: ImageSize | undefined): void {
    const parsed = parseAssetSrc(this.getSrc())
    if (parsed) {
      this.setSrc(createAssetSrc(parsed.assetId, size))
    }
  }

  createDOM(): HTMLElement {
    const div = document.createElement('div')
    div.className = 'lexical-image-container'
    return div
  }

  updateDOM(): false {
    return false
  }

  exportDOM(): DOMExportOutput {
    const element = document.createElement('img')
    element.setAttribute('src', this.__src)
    element.setAttribute('alt', this.__altText)
    return { element }
  }

  static importDOM(): DOMConversionMap | null {
    return {
      img: () => ({
        conversion: (domNode: HTMLElement) => {
          const src = domNode.getAttribute('src')
          const altText = domNode.getAttribute('alt') || ''
          if (!src) {
            return null
          }
          return { node: $createImageNode(src, altText) }
        },
        priority: 0,
      }),
    }
  }

  decorate(): ReactNode {
    return <ImageComponent src={this.__src} altText={this.__altText} nodeKey={this.__key} />
  }

  isInline(): boolean {
    return false // Block-level element
  }

  exportJSON(): SerializedImageNode {
    return {
      type: 'image',
      version: 1,
      src: this.__src,
      altText: this.__altText,
    }
  }

  static importJSON(serializedNode: SerializedImageNode): ImageNode {
    return $createImageNode(serializedNode.src, serializedNode.altText)
  }
}

export function $createImageNode(src: string, altText: string): ImageNode {
  return $applyNodeReplacement(new ImageNode(src, altText))
}

export function $isImageNode(node: LexicalNode | null | undefined): node is ImageNode {
  return node instanceof ImageNode
}
