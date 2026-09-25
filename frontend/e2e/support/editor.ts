import { expect, type Locator, type Page } from '@playwright/test'
import type { AssetDto } from '../../src/api/dto'
import { solidPng } from './ads'

/**
 * The rich-text editor every form shares (MediaEditor → MarkdownEditor, Tiptap underneath): finding
 * it, typing into it, its toolbar, adding a picture through it — and letting it hand its text over
 * to the form.
 */

/**
 * The labels the forms give their editor: most say « Description »; the team form and the team
 * page form name it after their placeholder.
 */
export const EDITOR_LABEL = {
  description: 'Description',
  team: 'Décrivez votre équipe, votre style de cyclisme ou votre communauté...',
  teamPage: 'Rédigez le contenu de votre page ici...',
} as const

/**
 * The editor's editable area within `scope` (a form, or the page's main): the contenteditable
 * textbox, by the accessible name its form gives it (MarkdownEditor's `ariaLabel`, pinned in
 * flow-team.e2e.ts).
 */
export const richText = (scope: Locator | Page, name: string = EDITOR_LABEL.description) =>
  scope.getByRole('textbox', { name, exact: true })

/**
 * Replaces the editor's whole content with `text`, typed as a user would (plain words: its markdown
 * is itself).
 */
export async function typeRichText(
  scope: Locator,
  text: string,
  name: string = EDITOR_LABEL.description
) {
  const editor = richText(scope, name)
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
 * MarkdownEditor hands its markdown to the form through a 150 ms debounce. Saving needs no pause:
 * the editor flushes it when it loses the focus, and clicking « Enregistrer » takes the focus
 * (words typed right before saving used to be lost — fixed 2026-09-25, flow-posts.e2e.ts). What
 * still needs one is removing an editor while an update is queued: deleting a trip stage right
 * after opening its tab crashes the form (pinned in flow-trips.e2e.ts). A person pauses before
 * that; this is the pause, taken on the page's fake clock instead of a sleep. It needs
 * `page.clock.install()` before the page's first `goto` (otherwise it throws), and time flows as
 * usual in between.
 */
export async function letEditorSettle(page: Page) {
  await page.clock.runFor(500)
}
