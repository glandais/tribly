import { useRef } from 'react'
import { useTranslation } from 'react-i18next'
import { RichTextEditor, useRichTextEditorContext } from '@mantine/tiptap'
import { IconPhoto } from '@tabler/icons-react'
import { Loader } from '@mantine/core'

export interface ImageUploadControlProps {
  onImageUpload: (file: File) => Promise<{ id: string; fileName: string } | null>
  isUploading?: boolean
}

export function ImageUploadControl({ onImageUpload, isUploading }: ImageUploadControlProps) {
  const { t } = useTranslation()
  const { editor } = useRichTextEditorContext()
  const inputRef = useRef<HTMLInputElement>(null)

  const handleClick = () => {
    inputRef.current?.click()
  }

  const handleFileSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file || !editor) return

    const result = await onImageUpload(file)
    if (result) {
      const altText = result.fileName.replace(/\.[^/.]+$/, '') // Remove extension
      // Insert *after* the selection, never over it: an asset node is an atom, and inserting one
      // leaves it selected. `insertContent` replaces the selection, so a second upload would
      // silently swap out the image the first one just added.
      const insertPos = editor.state.selection.to
      editor
        .chain()
        .focus()
        .insertContentAt(insertPos, {
          type: 'asset',
          attrs: {
            id: result.id,
            alt: altText,
            size: 'medium',
          },
        })
        .run()
    }
    // Reset input to allow selecting the same file again
    e.target.value = ''
  }

  return (
    <>
      <RichTextEditor.Control
        onClick={handleClick}
        disabled={isUploading}
        aria-label={t('editor.image')}
        title={t('editor.image')}
      >
        {isUploading ? <Loader size={16} /> : <IconPhoto size={16} />}
      </RichTextEditor.Control>
      <input
        ref={inputRef}
        type="file"
        accept="image/*,.heic,.heif"
        onChange={handleFileSelect}
        style={{ display: 'none' }}
      />
    </>
  )
}
