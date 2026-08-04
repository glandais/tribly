import { useCallback, useEffect, useMemo, useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { Box } from '@mantine/core'
import { RichTextEditor } from '@mantine/tiptap'
import { useEditor } from '@tiptap/react'
import StarterKit from '@tiptap/starter-kit'
import { Table } from '@tiptap/extension-table'
import TableRow from '@tiptap/extension-table-row'
import TableHeader from '@tiptap/extension-table-header'
import TableCell from '@tiptap/extension-table-cell'
import Placeholder from '@tiptap/extension-placeholder'
import { Markdown } from 'tiptap-markdown'
import '@mantine/tiptap/styles.css'

import type { AssetDto } from '@/api/dto'
import { AssetNode, AssetImagesProvider, markdownToEditor, ImageUploadControl } from './tiptap'
import './tiptap/tiptap.css'

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
  images?: AssetDto[]
}

export function MarkdownEditor({
  value = '',
  onChange,
  placeholder,
  minHeight = '200px',
  maxHeight = '1024px',
  disabled = false,
  ariaLabel: _ariaLabel,
  onImageUpload,
  isUploadingImage,
  images = [],
}: MarkdownEditorProps) {
  const { t } = useTranslation()

  // Track if we're updating from external value change
  const isExternalUpdate = useRef(false)

  // Debounced onChange to avoid performance issues during typing
  const debouncedOnChange = useMemo(
    () => debounce((markdown: string) => onChange?.(markdown), 150),
    [onChange]
  )

  // Wrapper that checks if we should skip the update
  const handleEditorChange = useCallback(
    (markdown: string) => {
      if (!isExternalUpdate.current) {
        debouncedOnChange(markdown)
      }
    },
    [debouncedOnChange]
  )

  const editor = useEditor({
    extensions: [
      StarterKit.configure({
        // Disable code block (not in original editor)
        codeBlock: false,
      }),
      Table.configure({ resizable: true }),
      TableRow,
      TableHeader,
      TableCell,
      Placeholder.configure({
        placeholder: placeholder || t('editor.placeholder'),
      }),
      Markdown.configure({
        // html: true is needed to parse <div data-type="asset"> tags from markdownToEditor()
        html: true,
        tightLists: true,
        bulletListMarker: '-',
      }),
      AssetNode,
    ],
    content: markdownToEditor(value),
    editable: !disabled,
    onUpdate: ({ editor }) => {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const storage = editor.storage as any
      const markdown = storage.markdown?.getMarkdown?.() || ''
      handleEditorChange(markdown)
    },
  })

  // Sync external value changes to editor
  useEffect(() => {
    if (editor && !editor.isDestroyed) {
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      const storage = editor.storage as any
      const currentMarkdown = storage.markdown?.getMarkdown?.() || ''
      if (value !== currentMarkdown) {
        isExternalUpdate.current = true
        editor.commands.setContent(markdownToEditor(value))
        // Reset flag after a short delay to allow the update to complete
        setTimeout(() => {
          isExternalUpdate.current = false
        }, 50)
      }
    }
  }, [value, editor])

  // Update editable state when disabled changes
  useEffect(() => {
    if (editor && !editor.isDestroyed) {
      editor.setEditable(!disabled)
    }
  }, [disabled, editor])

  return (
    <AssetImagesProvider images={images}>
      <Box
        className="tiptap-editor"
        style={
          {
            '--tiptap-min-height': minHeight,
            '--tiptap-max-height': maxHeight,
          } as React.CSSProperties
        }
      >
        <RichTextEditor
          editor={editor}
          styles={{
            root: {
              border: '1px solid var(--mantine-color-default-border)',
              borderRadius: 'var(--mantine-radius-xl)',
              boxShadow: 'var(--mantine-shadow-sm)',
              backgroundColor: 'var(--mantine-color-body)',
            },
            toolbar: {
              backgroundColor: 'var(--mantine-color-default-hover)',
              borderBottom: '1px solid var(--mantine-color-default-border)',
              borderRadius: 'var(--mantine-radius-xl) var(--mantine-radius-xl) 0 0',
            },
            // The content box reserves its height inline, NOT from `tiptap.css`. Two independent
            // things arrive after the first paint: the editor itself (`useEditor` returns null
            // until it is created, so there is no `.ProseMirror` in the DOM) and the page chunk's
            // stylesheet. With the min-height in the stylesheet, either one missing collapses the
            // zone to 0 px — measured: 150 px with the rule, 0 px without it. An inline style is
            // part of the markup, so the box is the right size from the very first frame.
            content: {
              borderRadius: '0 0 var(--mantine-radius-xl) var(--mantine-radius-xl)',
              minHeight,
              maxHeight,
              overflowY: 'auto',
              resize: 'vertical',
            },
          }}
        >
          <RichTextEditor.Toolbar sticky stickyOffset={0}>
            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Undo />
              <RichTextEditor.Redo />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Bold />
              <RichTextEditor.Italic />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.H1 />
              <RichTextEditor.H2 />
              <RichTextEditor.H3 />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.BulletList />
              <RichTextEditor.OrderedList />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Link />
              <RichTextEditor.Unlink />
            </RichTextEditor.ControlsGroup>

            <RichTextEditor.ControlsGroup>
              <RichTextEditor.Blockquote />
              <RichTextEditor.Hr />
            </RichTextEditor.ControlsGroup>

            {onImageUpload && (
              <RichTextEditor.ControlsGroup>
                <ImageUploadControl onImageUpload={onImageUpload} isUploading={isUploadingImage} />
              </RichTextEditor.ControlsGroup>
            )}
          </RichTextEditor.Toolbar>

          <RichTextEditor.Content />
        </RichTextEditor>
      </Box>
    </AssetImagesProvider>
  )
}
