import { expect, type Locator, type Page } from '@playwright/test'
import type { AssetDto } from '../../src/api/dto'
import { solidPng } from './ads'

/**
 * The rich-text editor every form shares (MediaEditor → MarkdownEditor, Tiptap underneath): finding
 * it, typing into it, its toolbar, adding a picture through it — and letting it hand its text over
 * to the form before saving.
 */

/**
 * The editor's editable area within `scope` (a form, or the page's main). It has no accessible
 * name — MarkdownEditor drops the `ariaLabel` it is given, pinned in flow-team.e2e.ts — so it is
 * found by what it is: the contenteditable textbox. A scope holds one.
 */
export const richText = (scope: Locator | Page) =>
  scope.locator('[contenteditable="true"][role="textbox"]')

/**
 * Replaces the editor's whole content with `text`, typed as a user would (plain words: its markdown
 * is itself). Call `letEditorSettle` before saving.
 */
export async function typeRichText(scope: Locator, text: string) {
  const editor = richText(scope)
  // useEditor() only creates the editor on the client: once it is in the DOM, it is live.
  await expect(editor).toBeVisible()
  await editor.click()
  await editor.press('ControlOrMeta+a')
  await editor.press('Delete')
  await editor.pressSequentially(text)
  await expect(editor).toHaveText(text)
}

/** A toolbar control of the editor, by its label (Mantine's own, in English: « Bold », « Link »…). */
export const toolbarButton = (scope: Locator, name: string) =>
  scope.getByRole('button', { name, exact: true })

/**
 * Adds a picture through the editor's « Image » toolbar control, the way a user does, waits until
 * the editor shows it (its alt is the file name without the extension), and returns the asset the
 * upload created.
 */
export async function addImage(
  page: Page,
  scope: Locator,
  name: string,
  colour: [number, number, number],
  [width, height]: [number, number] = [320, 240]
): Promise<AssetDto> {
  const upload = page.waitForResponse(
    (response) =>
      response.request().method() === 'POST' && /\/api\/teams\/[^/]+\/assets/.test(response.url())
  )
  const chooser = page.waitForEvent('filechooser')
  await toolbarButton(scope, 'Image').click()
  await (
    await chooser
  ).setFiles({ name, mimeType: 'image/png', buffer: solidPng(width, height, colour) })
  const response = await upload
  expect(response.ok(), `upload of ${name}`).toBe(true)
  await expect(richText(scope).getByRole('img', { name: name.replace(/\.png$/, '') })).toBeVisible()
  return (await response.json()) as AssetDto
}

/**
 * MarkdownEditor hands its markdown to the form through a 150 ms debounce, and submitting the form
 * does not flush it — an app defect (words typed within 150 ms of saving are lost), pinned in
 * flow-posts.e2e.ts. A person pauses before clicking « Enregistrer »; this is that pause, taken on
 * the page's fake clock instead of a sleep. It needs `page.clock.install()` before the page's first
 * `goto` (otherwise it throws), and time flows as usual in between.
 */
export async function letEditorSettle(page: Page) {
  await page.clock.runFor(500)
}
