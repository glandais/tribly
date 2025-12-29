import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import removeMd from 'remove-markdown'
import type { AssetDto } from '../../api/api'
import { isAssetSrc, parseAssetSrc } from '../../lib/assetMarkdown'
import { AssetImage } from './AssetImage'

export interface MarkdownDisplayProps {
  markdown: string
  className?: string
  preview?: boolean
  maxLength?: number
  images?: AssetDto[]
}

const safeProtocol = /^(https?|ircs?|mailto|xmpp|asset)$/i
/**
 * Make a URL safe.
 *
 * @satisfies {UrlTransform}
 * @param {string} value
 *   URL.
 * @returns {string}
 *   Safe URL.
 */
function urlTransform(value?: string): string {
  if (!value) {
    return ''
  }
  // Same as:
  // <https://github.com/micromark/micromark/blob/929275e/packages/micromark-util-sanitize-uri/dev/index.js#L34>
  // But without the `encode` part.
  const colon = value.indexOf(':')
  const questionMark = value.indexOf('?')
  const numberSign = value.indexOf('#')
  const slash = value.indexOf('/')

  if (
    // If there is no protocol, it’s relative.
    colon === -1 ||
    // If the first colon is after a `?`, `#`, or `/`, it’s not a protocol.
    (slash !== -1 && colon > slash) ||
    (questionMark !== -1 && colon > questionMark) ||
    (numberSign !== -1 && colon > numberSign) ||
    // It is a protocol, it should be allowed.
    safeProtocol.test(value.slice(0, colon))
  ) {
    return value
  }

  return ''
}

export function MarkdownDisplay({
  markdown,
  className = '',
  preview = false,
  maxLength = 150,
  images = [],
}: MarkdownDisplayProps) {
  if (!markdown) {
    return null
  }

  // Preview mode: strip markdown and truncate
  if (preview) {
    const plainText = removeMd(markdown)
    const truncated =
      plainText.length > maxLength ? plainText.slice(0, maxLength) + '...' : plainText
    return <p className={className}>{truncated}</p>
  }

  // Full markdown rendering - asset URLs handled at render time via AssetImage
  return (
    <div
      className={`markdown-display prose prose-gray max-w-none ${className}`}
      style={{
        // Custom prose styles to match MarkdownEditor theme
        fontSize: '1rem',
        lineHeight: '1.75',
      }}
    >
      <ReactMarkdown
        remarkPlugins={[remarkGfm]}
        urlTransform={urlTransform} // Preserve asset: URLs without sanitization
        components={{
          // Images - detect asset: URLs and render via AssetImage
          img: ({ src, alt, ...props }) => {
            // Handle asset: URLs - render via AssetImage component
            if (src && isAssetSrc(src)) {
              const parsed = parseAssetSrc(src)
              if (parsed) {
                return (
                  <AssetImage
                    assetId={parsed.assetId}
                    images={images}
                    size={parsed.size}
                    altText={alt}
                    className="my-4"
                  />
                )
              }
            }

            // Regular URLs - render normal img tag
            return (
              <img
                src={src}
                alt={alt}
                className="max-w-full h-auto rounded-lg my-4"
                loading="lazy"
                {...props}
              />
            )
          },
          // Headings
          h1: ({ ...props }) => (
            <h1 className="text-3xl font-bold mb-4 mt-6 text-gray-900 tracking-tight" {...props} />
          ),
          h2: ({ ...props }) => (
            <h2 className="text-2xl font-bold mb-3 mt-5 text-gray-900 tracking-tight" {...props} />
          ),
          h3: ({ ...props }) => (
            <h3 className="text-xl font-bold mb-2 mt-4 text-gray-900 tracking-tight" {...props} />
          ),
          // Paragraphs
          p: ({ ...props }) => <p className="mb-2 text-gray-900 leading-relaxed" {...props} />,
          // Lists
          ul: ({ ...props }) => (
            <ul className="list-disc list-inside mb-2 text-gray-900" {...props} />
          ),
          ol: ({ ...props }) => (
            <ol className="list-decimal list-inside mb-2 text-gray-900" {...props} />
          ),
          li: ({ ...props }) => <li className="mb-1" {...props} />,
          // Links
          a: ({ ...props }) => (
            <a
              className="text-cyan-600 hover:text-cyan-700 underline cursor-pointer transition-colors"
              target="_blank"
              rel="noopener noreferrer"
              {...props}
            />
          ),
          // Code
          code: ({ className, children, ...props }) => {
            const isInline = !className
            if (isInline) {
              return (
                <code
                  className="bg-gray-100 text-pink-600 px-1.5 py-0.5 rounded font-mono text-sm"
                  {...props}
                >
                  {children}
                </code>
              )
            }
            return (
              <code
                className="block bg-gray-900 text-gray-100 p-4 rounded-lg font-mono text-sm my-4 overflow-x-auto"
                {...props}
              >
                {children}
              </code>
            )
          },
          // Blockquotes
          blockquote: ({ ...props }) => (
            <blockquote
              className="border-l-4 border-cyan-500 pl-4 italic text-gray-700 my-4"
              {...props}
            />
          ),
          // Tables (from remark-gfm)
          table: ({ ...props }) => (
            <div className="overflow-x-auto my-4">
              <table className="min-w-full divide-y divide-gray-200" {...props} />
            </div>
          ),
          thead: ({ ...props }) => <thead className="bg-gray-50" {...props} />,
          tbody: ({ ...props }) => (
            <tbody className="bg-white divide-y divide-gray-200" {...props} />
          ),
          tr: ({ ...props }) => <tr {...props} />,
          th: ({ ...props }) => (
            <th
              className="px-3 py-2 text-left text-xs font-medium text-gray-500 uppercase tracking-wider"
              {...props}
            />
          ),
          td: ({ ...props }) => <td className="px-3 py-2 text-sm text-gray-900" {...props} />,
          // Horizontal rule
          hr: ({ ...props }) => <hr className="my-4 border-gray-300" {...props} />,
          // Strong/bold
          strong: ({ ...props }) => <strong className="font-bold" {...props} />,
          // Emphasis/italic
          em: ({ ...props }) => <em className="italic" {...props} />,
        }}
      >
        {markdown}
      </ReactMarkdown>
    </div>
  )
}
