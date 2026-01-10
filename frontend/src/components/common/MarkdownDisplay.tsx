import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm'
import remarkDirective from 'remark-directive'
import removeMd from 'remove-markdown'
import {
  Text,
  Box,
  Title,
  Anchor,
  Table,
  Divider,
  Code,
  Blockquote,
  List,
  Image,
} from '@mantine/core'
import type { AssetDto } from '@/api/dto'
import type { ImageSize } from '../../lib/assetMarkdown'
import { remarkAssetDirective } from '../../lib/remarkAssetDirective'
import { AssetImage } from './AssetImage'

export interface MarkdownDisplayProps {
  markdown: string
  preview?: boolean
  maxLength?: number
  images?: AssetDto[]
}

export function MarkdownDisplay({
  markdown,
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
    return (
      <Text size="sm" c="dimmed">
        {truncated}
      </Text>
    )
  }

  // Full markdown rendering - asset URLs handled at render time via AssetImage
  return (
    <Box fz="md" lh={1.75}>
      <ReactMarkdown
        remarkPlugins={[remarkGfm, remarkDirective, remarkAssetDirective]}
        components={{
          // Asset directive - handles ::asset{id="..." size="..." alt="..."}
          // @ts-expect-error - custom component from remark-directive
          'asset-image': ({ id, size, alt }: { id?: string; size?: string; alt?: string }) => {
            if (!id) return null
            return (
              <Box my="md">
                <AssetImage assetId={id} images={images} size={size as ImageSize} altText={alt} />
              </Box>
            )
          },
          // Regular image URLs
          img: ({ src, alt, ...props }) => (
            <Image src={src} alt={alt} maw="100%" h="auto" radius="md" my="md" {...props} />
          ),
          // Headings
          h1: ({ children }) => (
            <Title order={1} mb="md" mt="lg" style={{ letterSpacing: '-0.025em' }}>
              {children}
            </Title>
          ),
          h2: ({ children }) => (
            <Title order={2} mb="sm" mt="lg" style={{ letterSpacing: '-0.025em' }}>
              {children}
            </Title>
          ),
          h3: ({ children }) => (
            <Title order={3} mb="xs" mt="md" style={{ letterSpacing: '-0.025em' }}>
              {children}
            </Title>
          ),
          // Paragraphs
          p: ({ children }) => (
            <Text component="p" mb="xs" lh={1.625}>
              {children}
            </Text>
          ),
          // Lists
          ul: ({ children }) => (
            <List type="unordered" mb="xs">
              {children}
            </List>
          ),
          ol: ({ children }) => (
            <List type="ordered" mb="xs">
              {children}
            </List>
          ),
          li: ({ children }) => <List.Item mb={4}>{children}</List.Item>,
          // Links
          a: ({ href, children }) => (
            <Anchor href={href} target="_blank" rel="noopener noreferrer" c="cyan.6">
              {children}
            </Anchor>
          ),
          // Code
          code: ({ className, children }) => {
            const isInline = !className
            if (isInline) {
              return (
                <Code bg="gray.1" c="pink.6" px={6} py={2} fz="sm">
                  {children}
                </Code>
              )
            }
            return (
              <Code
                block
                bg="dark.8"
                c="gray.1"
                p="md"
                fz="sm"
                my="md"
                style={{ overflowX: 'auto' }}
              >
                {children}
              </Code>
            )
          },
          // Blockquotes
          blockquote: ({ children }) => (
            <Blockquote color="cyan" my="md" fs="italic">
              {children}
            </Blockquote>
          ),
          // Tables (from remark-gfm)
          table: ({ children }) => (
            <Box style={{ overflowX: 'auto' }} my="md">
              <Table striped highlightOnHover withTableBorder>
                {children}
              </Table>
            </Box>
          ),
          thead: ({ children }) => <Table.Thead bg="gray.0">{children}</Table.Thead>,
          tbody: ({ children }) => <Table.Tbody>{children}</Table.Tbody>,
          tr: ({ children }) => <Table.Tr>{children}</Table.Tr>,
          th: ({ children }) => (
            <Table.Th
              px="sm"
              py="xs"
              fz="xs"
              fw={500}
              c="gray.6"
              tt="uppercase"
              style={{ letterSpacing: '0.05em' }}
            >
              {children}
            </Table.Th>
          ),
          td: ({ children }) => (
            <Table.Td px="sm" py="xs" fz="sm">
              {children}
            </Table.Td>
          ),
          // Horizontal rule
          hr: () => <Divider my="md" color="gray.3" />,
          // Strong/bold
          strong: ({ children }) => (
            <Text component="strong" fw={700}>
              {children}
            </Text>
          ),
          // Emphasis/italic
          em: ({ children }) => (
            <Text component="em" fs="italic">
              {children}
            </Text>
          ),
        }}
      >
        {markdown}
      </ReactMarkdown>
    </Box>
  )
}
