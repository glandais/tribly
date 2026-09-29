import { describe, it, expect, vi } from 'vitest'
import type { Editor } from '@tiptap/react'
import { imageFiles, uploadAndInsert } from './insertAsset'

function fileList(...files: File[]): FileList {
  return files as unknown as FileList
}

/** Just enough of an Editor to record where each asset lands; each insert advances by one. */
function fakeEditor() {
  const inserted: { pos: number; id: string; alt: string }[] = []
  const state = { selection: { to: 0 }, doc: { content: { size: 100 } } }
  const editor = {
    isDestroyed: false,
    state,
    chain: () => {
      const chain = {
        focus: () => chain,
        insertContentAt: (pos: number, node: { attrs: { id: string; alt: string } }) => {
          inserted.push({ pos, id: node.attrs.id, alt: node.attrs.alt })
          state.selection.to = pos + 1
          return chain
        },
        run: () => true,
      }
      return chain
    },
  }
  return { editor: editor as unknown as Editor, inserted }
}

describe('imageFiles', () => {
  it('keeps images, including a HEIC handed over without a MIME type', () => {
    const photo = new File([''], 'a.jpg', { type: 'image/jpeg' })
    const heic = new File([''], 'IMG_1.HEIC', { type: '' })
    const pdf = new File([''], 'doc.pdf', { type: 'application/pdf' })
    expect(imageFiles(fileList(photo, heic, pdf))).toEqual([photo, heic])
  })

  it('is empty for no files at all', () => {
    expect(imageFiles(undefined)).toEqual([])
  })
})

describe('uploadAndInsert', () => {
  it('inserts each image after the previous one, in the order dropped', async () => {
    const { editor, inserted } = fakeEditor()
    const upload = vi.fn(async (file: File) => ({ id: file.name, fileName: `${file.name}.jpg` }))
    await uploadAndInsert(editor, 10, [new File([''], 'one'), new File([''], 'two')], upload)
    expect(inserted).toEqual([
      { pos: 10, id: 'one', alt: 'one' },
      { pos: 11, id: 'two', alt: 'two' },
    ])
  })

  it('skips a failed upload and goes on with the next one', async () => {
    const { editor, inserted } = fakeEditor()
    const upload = vi
      .fn()
      .mockResolvedValueOnce(null)
      .mockResolvedValueOnce({ id: 'ok', fileName: 'ok.png' })
    await uploadAndInsert(editor, 5, [new File([''], 'bad'), new File([''], 'ok')], upload)
    expect(inserted).toEqual([{ pos: 5, id: 'ok', alt: 'ok' }])
  })
})
