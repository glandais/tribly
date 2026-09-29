import type { Editor } from '@tiptap/react'

export type ImageUploadHandler = (file: File) => Promise<{ id: string; fileName: string } | null>

/**
 * Inserts an uploaded image at `pos` as an asset node, and returns the position right after it.
 *
 * Insert *at a position*, never over the selection: an asset node is an atom, and inserting one
 * leaves it selected. `insertContent` replaces the selection, so a second upload would silently
 * swap out the image the first one just added.
 */
export function insertAsset(
  editor: Editor,
  pos: number,
  asset: { id: string; fileName: string }
): number {
  editor
    .chain()
    .focus()
    .insertContentAt(pos, {
      type: 'asset',
      attrs: {
        id: asset.id,
        alt: asset.fileName.replace(/\.[^/.]+$/, ''), // Remove extension
        size: 'medium',
      },
    })
    .run()
  return editor.state.selection.to
}

/**
 * The images among dropped or pasted files — what the toolbar's file picker would have accepted
 * (`image/*`, plus HEIC/HEIF, which some systems hand over without a MIME type).
 */
export function imageFiles(files: FileList | null | undefined): File[] {
  return Array.from(files ?? []).filter(
    (file) => file.type.startsWith('image/') || /\.(heic|heif)$/i.test(file.name)
  )
}

/**
 * Uploads `files` one after the other and inserts each image where the previous one ended, so a
 * multi-image drop keeps its order. Sequential on purpose: each upload appends to the form's asset
 * list, and parallel writes would race on it. A failed upload is skipped; the handler reports it.
 */
export async function uploadAndInsert(
  editor: Editor,
  pos: number,
  files: File[],
  onImageUpload: ImageUploadHandler
): Promise<void> {
  let at = pos
  for (const file of files) {
    const result = await onImageUpload(file)
    if (!result || editor.isDestroyed) continue
    at = insertAsset(editor, Math.min(at, editor.state.doc.content.size), result)
  }
}
