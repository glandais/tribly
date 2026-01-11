import React, { useCallback, useRef, useEffect, useMemo } from 'react'
import { useTranslation } from 'react-i18next'
import { Box, ActionIcon, useComputedColorScheme } from '@mantine/core'
import {
  MDXEditor,
  headingsPlugin,
  listsPlugin,
  quotePlugin,
  linkPlugin,
  linkDialogPlugin,
  tablePlugin,
  thematicBreakPlugin,
  markdownShortcutPlugin,
  directivesPlugin,
  toolbarPlugin,
  UndoRedo,
  BoldItalicUnderlineToggles,
  BlockTypeSelect,
  CreateLink,
  ListsToggle,
  InsertTable,
  Separator,
  type MDXEditorMethods,
} from '@mdxeditor/editor'
import '@mdxeditor/editor/style.css'
import './mdxeditor/mdxeditor.css'
import type { AssetDto } from '@/api/dto'
import { createAssetDirective } from '@/lib/assetMarkdown'
import { AssetDirectiveDescriptor, AssetImagesProvider } from './mdxeditor'
import { IconPhoto } from '@tabler/icons-react'
import { LoadingSpinner } from './LoadingSpinner'

// Debounce utility
function debounce<T extends (...args: Parameters<T>) => void>(
  fn: T,
  delay: number
): (...args: Parameters<T>) => void {
  let timeoutId: ReturnType<typeof setTimeout> | null = null
  return (...args: Parameters<T>) => {
    if (timeoutId) clearTimeout(timeoutId)
    timeoutId = setTimeout(() => fn(...args), delay)
  }
}

// ============================================================================
// Custom Asset Upload Toolbar Button
// ============================================================================

interface AssetUploadButtonProps {
  editorRef: React.RefObject<MDXEditorMethods | null>
  onImageUpload?: (file: File) => Promise<{ id: string; fileName: string } | null>
  isUploadingImage?: boolean
}

function AssetUploadButton({ editorRef, onImageUpload, isUploadingImage }: AssetUploadButtonProps) {
  const { t } = useTranslation()
  const imageInputRef = useRef<HTMLInputElement>(null)

  const handleClick = useCallback(() => {
    imageInputRef.current?.click()
  }, [])

  const handleImageSelect = useCallback(
    async (e: React.ChangeEvent<HTMLInputElement>) => {
      const file = e.target.files?.[0]
      if (!file || !onImageUpload || !editorRef.current) return

      const result = await onImageUpload(file)
      if (result) {
        const altText = result.fileName.replace(/\.[^/.]+$/, '') // Remove extension

        // Insert markdown directly since we can't access insertDirective$ from outside
        const directiveMarkdown = `\n${createAssetDirective(result.id, altText)}\n`
        editorRef.current.insertMarkdown(directiveMarkdown)
      }
      // Reset input to allow selecting the same file again
      e.target.value = ''
    },
    [editorRef, onImageUpload]
  )

  if (!onImageUpload) return null

  return (
    <>
      <ActionIcon
        variant="subtle"
        color="gray"
        onClick={handleClick}
        disabled={isUploadingImage}
        title={t('editor.image')}
        aria-label={t('editor.image')}
      >
        {isUploadingImage ? <LoadingSpinner size="sm" color="gray" /> : <IconPhoto size={16} />}
      </ActionIcon>
      <input
        ref={imageInputRef}
        type="file"
        accept="image/*"
        onChange={handleImageSelect}
        style={{ display: 'none' }}
      />
    </>
  )
}

// ============================================================================
// Toolbar Contents Component
// ============================================================================

interface ToolbarContentsProps {
  editorRef: React.RefObject<MDXEditorMethods | null>
  onImageUpload?: (file: File) => Promise<{ id: string; fileName: string } | null>
  isUploadingImage?: boolean
}

function ToolbarContents({ editorRef, onImageUpload, isUploadingImage }: ToolbarContentsProps) {
  return (
    <>
      <UndoRedo />
      <Separator />
      <BoldItalicUnderlineToggles options={['Bold', 'Italic']} />
      <Separator />
      <BlockTypeSelect />
      <Separator />
      <ListsToggle />
      <Separator />
      <CreateLink />
      <InsertTable />
      <AssetUploadButton
        editorRef={editorRef}
        onImageUpload={onImageUpload}
        isUploadingImage={isUploadingImage}
      />
    </>
  )
}

// ============================================================================
// Main MarkdownEditor Component
// ============================================================================

export interface MarkdownEditorProps {
  value: string
  onChange?: (value: string) => void
  placeholder?: string
  minHeight?: string
  maxHeight?: string
  disabled?: boolean
  ariaLabel?: string
  onImageUpload?: (file: File) => Promise<{ id: string; fileName: string } | null>
  isUploadingImage?: boolean
  images?: AssetDto[] // For resolving asset:id URLs to actual image URLs
}

export function MarkdownEditor({
  value = '',
  onChange,
  placeholder,
  minHeight = '200px',
  maxHeight = '500px',
  disabled = false,
  // ariaLabel is kept in props interface for backwards compatibility but MDXEditor handles accessibility internally
  ariaLabel: _ariaLabel,
  onImageUpload,
  isUploadingImage,
  images = [],
}: MarkdownEditorProps) {
  const { t } = useTranslation()
  const colorScheme = useComputedColorScheme('light')
  const editorRef = useRef<MDXEditorMethods>(null)

  // Debounced onChange to avoid performance issues during typing
  const debouncedOnChange = useMemo(
    () => debounce((markdown: string) => onChange?.(markdown), 150),
    [onChange]
  )

  const handleChange = useCallback(
    (markdown: string) => {
      debouncedOnChange(markdown)
    },
    [debouncedOnChange]
  )

  // Sync external value changes to editor (e.g., when loading existing content)
  useEffect(() => {
    if (editorRef.current) {
      const currentMarkdown = editorRef.current.getMarkdown()
      if (value !== currentMarkdown) {
        editorRef.current.setMarkdown(value)
      }
    }
  }, [value])

  // Use CSS custom properties for dynamic height
  const editorStyle = {
    '--mdx-min-height': minHeight,
    '--mdx-max-height': maxHeight,
  } as React.CSSProperties

  return (
    <AssetImagesProvider images={images}>
      <Box
        style={{
          ...editorStyle,
          border: '1px solid var(--mantine-color-default-border)',
          borderRadius: 'var(--mantine-radius-xl)',
          overflow: 'hidden',
          boxShadow: 'var(--mantine-shadow-sm)',
          backgroundColor: 'var(--mantine-color-body)',
          transition: 'box-shadow 300ms ease',
        }}
      >
        <MDXEditor
          ref={editorRef}
          className={
            colorScheme === 'dark' ? 'dark-theme dark-editor mdxeditor-custom' : 'mdxeditor-custom'
          }
          markdown={value}
          onChange={handleChange}
          readOnly={disabled}
          placeholder={placeholder || t('editor.placeholder')}
          plugins={[
            headingsPlugin(),
            listsPlugin(),
            quotePlugin(),
            linkPlugin(),
            linkDialogPlugin(),
            tablePlugin(),
            thematicBreakPlugin(),
            markdownShortcutPlugin(),
            directivesPlugin({
              directiveDescriptors: [AssetDirectiveDescriptor],
            }),
            toolbarPlugin({
              toolbarContents: () => (
                <ToolbarContents
                  editorRef={editorRef}
                  onImageUpload={onImageUpload}
                  isUploadingImage={isUploadingImage}
                />
              ),
            }),
          ]}
        />
      </Box>
    </AssetImagesProvider>
  )
}
