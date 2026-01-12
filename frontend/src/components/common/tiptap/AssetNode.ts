import { Node, mergeAttributes } from '@tiptap/core'
import { ReactNodeViewRenderer } from '@tiptap/react'
import { AssetNodeView } from './AssetNodeView'
import { DEFAULT_IMAGE_SIZE } from '@/lib/assetMarkdown'

declare module '@tiptap/core' {
  interface Commands<ReturnType> {
    asset: {
      insertAsset: (attrs: { id: string; alt?: string; size?: string }) => ReturnType
    }
  }
}

export interface AssetNodeOptions {
  HTMLAttributes: Record<string, unknown>
}

export const AssetNode = Node.create<AssetNodeOptions>({
  name: 'asset',
  group: 'block',
  atom: true,
  draggable: true,

  addOptions() {
    return {
      HTMLAttributes: {},
    }
  },

  addAttributes() {
    return {
      id: {
        default: null,
        parseHTML: (element: HTMLElement) =>
          element.getAttribute('data-id') || element.getAttribute('id'),
        renderHTML: (attributes: Record<string, unknown>) => ({ 'data-id': attributes.id }),
      },
      size: {
        default: DEFAULT_IMAGE_SIZE,
        parseHTML: (element: HTMLElement) =>
          element.getAttribute('data-size') || element.getAttribute('size') || DEFAULT_IMAGE_SIZE,
        renderHTML: (attributes: Record<string, unknown>) => ({ 'data-size': attributes.size }),
      },
      alt: {
        default: '',
        parseHTML: (element: HTMLElement) =>
          element.getAttribute('data-alt') || element.getAttribute('alt') || '',
        renderHTML: (attributes: Record<string, unknown>) => ({ 'data-alt': attributes.alt }),
      },
    }
  },

  parseHTML() {
    return [
      {
        // Match <div data-type="asset" ...>
        tag: 'div[data-type="asset"]',
      },
    ]
  },

  renderHTML({ HTMLAttributes }: { HTMLAttributes: Record<string, unknown> }) {
    return [
      'div',
      mergeAttributes(this.options.HTMLAttributes, HTMLAttributes, { 'data-type': 'asset' }),
    ]
  },

  addNodeView() {
    return ReactNodeViewRenderer(AssetNodeView)
  },

  addCommands() {
    return {
      insertAsset:
        (attrs: { id: string; alt?: string; size?: string }) =>
        ({ commands }) => {
          return commands.insertContent({
            type: this.name,
            attrs,
          })
        },
    }
  },

  addStorage() {
    return {
      markdown: {
        serialize(
          state: { write: (text: string) => void },
          node: { attrs: Record<string, unknown> }
        ) {
          const parts: string[] = [`id="${node.attrs.id}"`]
          if (node.attrs.size && node.attrs.size !== DEFAULT_IMAGE_SIZE) {
            parts.push(`size="${node.attrs.size}"`)
          }
          if (node.attrs.alt) {
            const escapedAlt = String(node.attrs.alt).replace(/"/g, '\\"')
            parts.push(`alt="${escapedAlt}"`)
          }
          state.write(`::asset{${parts.join(' ')}}\n\n`)
        },
        parse: {
          // Parsing handled via pre-processing with markdownToEditor()
        },
      },
    }
  },
})
