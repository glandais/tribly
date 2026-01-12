import { ASSET_DIRECTIVE_REGEX, parseDirectiveAttributes } from '@/lib/assetMarkdown'

/**
 * Convert ::asset{} directives to HTML for Tiptap parsing.
 * Input: ::asset{id="abc123" size="medium" alt="description"}
 * Output: <div data-type="asset" data-id="abc123" data-size="medium" data-alt="description"></div>
 *
 * We use a <div> with data attributes because markdown-it handles standard HTML
 * elements more reliably than custom elements like <asset-node>.
 */
export function markdownToEditor(markdown: string): string {
  return markdown.replace(ASSET_DIRECTIVE_REGEX, (_, attributeString: string) => {
    const attrs = parseDirectiveAttributes(attributeString)
    const dataAttrs = Object.entries(attrs)
      .map(([key, value]) => `data-${key}="${value}"`)
      .join(' ')
    return `<div data-type="asset" ${dataAttrs}></div>`
  })
}
