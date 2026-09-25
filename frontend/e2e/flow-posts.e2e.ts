import type { Locator } from '@playwright/test'
import type { CommentListResponse } from '../src/api/dto'
import { apiGet } from './support/api'
import { addMember, markdownMedia, newTeam, newUser, signIn } from './support/data'
import { addImage, richText, toolbarButton, typeRichText } from './support/editor'
import { expect, test, unique } from './support/fixtures'
import { fetchPost, findPost, newPost } from './support/posts'
import { entityCard, hydrated, openActionsMenu, pageAs, toasts } from './support/ui'

/**
 * Publications (posts), the nominal journey through the UI only: a team admin writes a post in the
 * rich-text editor (heading, bold, link, list, uploaded image), publishes it; it shows in the team
 * feed and on a member's home feed; the member opens it and comments; the author edits the post,
 * deletes the comment, then deletes the post.
 *
 * The rendered page is checked element by element (a heading is a heading, the link a link with its
 * href, the image an <img> that actually loaded), and the stored markdown through the API.
 */

const LINK_URL = 'https://example.com/parcours-e2e'
const IMAGE_NAME = 'photo-e2e-sortie.png'
const IMAGE_ALT = 'photo-e2e-sortie'

/** Each locator's (single) element comes after the previous one's in the document. */
async function expectInDocumentOrder(locators: Locator[]) {
  const handles = await Promise.all(locators.map((l) => l.elementHandle()))
  for (let i = 1; i < handles.length; i++) {
    const follows = await handles[i - 1]!.evaluate(
      (previous, next) =>
        !!(previous.compareDocumentPosition(next as Node) & Node.DOCUMENT_POSITION_FOLLOWING),
      handles[i]!
    )
    expect(follows, `block ${i} comes after block ${i - 1}`).toBe(true)
  }
}

/** An <img> the browser really decoded — not a broken-image placeholder. */
async function expectImageLoaded(image: Locator) {
  await expect(image).toBeVisible()
  await expect
    .poll(() =>
      image.evaluate(
        (el) => (el as HTMLImageElement).complete && (el as HTMLImageElement).naturalWidth
      )
    )
    .toBeGreaterThan(0)
}

test('a post goes from the editor to the feeds, gets a comment, is edited, then deleted', async ({
  page,
  context,
  browser,
}) => {
  // The author owns the team, so is its ADMIN: a deleted post stays listed to them, flagged.
  const author = await newUser(unique('Autrice'))
  const member = await newUser(unique('Lectrice'))
  // Rides off: the feed's create button then opens the post editor directly.
  const team = await newTeam(author, unique('Publications'), {
    addMemberAllowed: true,
    enableRides: false,
  })
  await addMember(author, team.slug, member)

  const title = unique('Sortie du dimanche')
  const main = page.getByRole('main')
  let postSlug = ''
  await signIn(context, author)
  // The member reads in a browser of their own.
  const reader = await pageAs(browser, member)
  const memberMain = reader.page.getByRole('main')
  const feedCard = (name: string, scope: Locator = main) => entityCard(scope, name)

  await test.step('the author writes the post in the rich-text editor and publishes it', async () => {
    await page.goto(`/equipes/${team.slug}`)
    await expect(main.getByRole('heading', { name: "Fil d'actualités" })).toBeVisible()
    const create = main.getByRole('link', { name: 'Nouvelle publication' })
    await hydrated(create)
    await create.click()
    await expect(
      main.getByRole('heading', { level: 1, name: 'Nouvelle publication' })
    ).toBeVisible()

    const titleInput = main.getByRole('textbox', { name: 'Titre' })
    await hydrated(titleInput)
    await titleInput.fill(title)

    const editor = richText(main)
    await editor.click()
    // Heading
    await toolbarButton(main, 'Heading 2').click()
    await page.keyboard.type('Au programme')
    await page.keyboard.press('Enter')
    // Bold, in the middle of a paragraph
    await page.keyboard.type('Rendez-vous ')
    await toolbarButton(main, 'Bold').click()
    await page.keyboard.type('à 8 h précises')
    await toolbarButton(main, 'Bold').click()
    await page.keyboard.type(' devant le club.')
    await page.keyboard.press('Enter')
    // Link: type the words, select them, then the link control
    await page.keyboard.type('Voir le parcours')
    await page.keyboard.press('Shift+Home')
    await toolbarButton(main, 'Link').click()
    const urlInput = page.getByRole('textbox', { name: 'Enter URL' })
    await urlInput.fill(LINK_URL)
    await page.getByRole('button', { name: 'Save', exact: true }).click()
    // Saving hands the focus back to the editor, the linked words still selected.
    await expect(urlInput).toBeHidden()
    await expect(editor).toBeFocused()
    await expect(editor.getByRole('link', { name: 'Voir le parcours' })).toBeVisible()
    // The selection comes back after the focus: End pressed before it would be undone by it, and
    // Enter would then replace the linked words.
    const selection = () => page.evaluate(() => window.getSelection()?.toString() ?? '')
    await expect.poll(selection).toBe('Voir le parcours')
    await page.keyboard.press('End')
    await expect.poll(selection).toBe('')
    await page.keyboard.press('Enter')
    // Bullet list
    await toolbarButton(main, 'Bullet list').click()
    await page.keyboard.type('Casque')
    await page.keyboard.press('Enter')
    await page.keyboard.type('Gilet jaune')
    await page.keyboard.press('Enter')
    await page.keyboard.press('Enter')

    // Image, through the editor's own upload control; the editor shows it in place.
    await addImage(page, main, IMAGE_NAME, [30, 120, 200], [96, 64])
    await expectImageLoaded(editor.getByRole('img', { name: IMAGE_ALT }))

    // Formatting is visible in the editor itself.
    await expect(editor.locator('h2')).toHaveText('Au programme')
    await expect(editor.locator('strong')).toHaveText('à 8 h précises')
    await expect(editor.locator('a')).toHaveAttribute('href', LINK_URL)
    await expect(editor.locator('ul > li')).toHaveText(['Casque', 'Gilet jaune'])

    await main.getByRole('radio', { name: 'Publié' }).check()
    const submit = main.getByRole('button', { name: 'Créer la publication' })
    await expect(submit).toBeEnabled()
    await submit.click()

    await expect(page.getByText('Publication créée avec succès')).toBeVisible()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/articles/[^/]+$`))
    postSlug = new URL(page.url()).pathname.split('/').pop()!
  })

  await test.step('the post page renders the formatting, the link and the image', async () => {
    await expect(main.getByRole('heading', { level: 2, name: title })).toBeVisible()
    await expect(main.getByText('Publié', { exact: true })).toBeVisible()
    await expect(main.getByRole('heading', { level: 2, name: 'Au programme' })).toBeVisible()
    await expect(main.locator('strong', { hasText: 'à 8 h précises' })).toBeVisible()
    // Markdown syntax must not leak into the page.
    await expect(main.getByText('**', { exact: false })).toHaveCount(0)
    await expect(main.getByText('## Au programme')).toHaveCount(0)
    const link = main.getByRole('link', { name: 'Voir le parcours' })
    await expect(link).toHaveAttribute('href', LINK_URL)
    await expect(link).toHaveAttribute('target', '_blank')
    await expect(main.getByRole('listitem')).toHaveText(['Casque', 'Gilet jaune'])
    await expectImageLoaded(main.getByRole('img', { name: IMAGE_ALT }))
    // In the order they were written.
    await expectInDocumentOrder([
      main.getByRole('heading', { level: 2, name: 'Au programme' }),
      main.locator('strong', { hasText: 'à 8 h précises' }),
      link,
      main.getByRole('list'),
      main.getByRole('img', { name: IMAGE_ALT }),
    ])

    // What was stored: the markdown the editor wrote, and the picture it references.
    const post = await fetchPost(author, team.slug, postSlug)
    expect(post.name).toBe(title)
    expect(post.status).toBe('PUBLISHED')
    expect(post.deleted).toBe(false)
    const { markdown, assets } = post.media
    expect(markdown).toMatch(/^## Au programme$/m)
    expect(markdown).toContain('Rendez-vous **à 8 h précises** devant le club.')
    expect(markdown).toContain(`[Voir le parcours](${LINK_URL})`)
    expect(markdown).toMatch(/^- Casque\n- Gilet jaune$/m)
    expect(assets.images).toHaveLength(1)
    expect(assets.images[0].fileName).toBe(IMAGE_NAME)
    expect(markdown).toContain(`::asset{id="${assets.images[0].id}"`)
  })

  await test.step('it shows in the team feed', async () => {
    await page.goto(`/equipes/${team.slug}`)
    const card = feedCard(title)
    await expect(card).toBeVisible()
    await expect(card.getByText('Publication', { exact: true })).toBeVisible()
    await expect(card.getByText('0 commentaire', { exact: true })).toBeVisible()
  })

  await test.step('a member finds it on the home feed, opens it and comments', async () => {
    const { page: memberPage } = reader
    await memberPage.goto('/')
    await expect(memberMain.getByRole('heading', { name: 'Dernières publications' })).toBeVisible()
    const card = feedCard(title, memberMain)
    await expect(card).toBeVisible()
    await hydrated(card)
    await card.click()

    await expect(memberPage).toHaveURL(new RegExp(`/equipes/${team.slug}/articles/${postSlug}$`))
    await expect(memberMain.getByRole('heading', { level: 2, name: title })).toBeVisible()
    await expectImageLoaded(memberMain.getByRole('img', { name: IMAGE_ALT }))
    // A plain member has no edit controls.
    await expect(memberMain.getByRole('link', { name: 'Modifier' })).toHaveCount(0)

    await expect(memberMain.getByRole('heading', { name: 'Commentaires (0)' })).toBeVisible()
    const box = memberMain.getByRole('textbox', { name: 'Écrivez un commentaire...' })
    await hydrated(box)
    await box.fill('Je viens, avec le gilet !')
    await memberMain.getByRole('button', { name: 'Envoyer le commentaire' }).click()
    await expect(memberMain.getByRole('heading', { name: 'Commentaires (1)' })).toBeVisible()
    await expect(memberMain.getByText('Je viens, avec le gilet !', { exact: true })).toBeVisible()
    await expect(memberMain.getByText(member.user.displayName, { exact: true })).toBeVisible()
    await expect(box).toHaveValue('')

    const comments = await apiGet<CommentListResponse>(
      member,
      `/api/teams/${team.slug}/posts/${postSlug}/comments`
    )
    expect(comments.total).toBe(1)
    expect(comments.items[0].content).toBe('Je viens, avec le gilet !')
    expect(comments.items[0].author.id).toBe(member.user.id)

    // The count on the feed card follows.
    await memberPage.goto(`/equipes/${team.slug}`)
    await expect(
      feedCard(title, memberMain).getByText('1 commentaire', { exact: true })
    ).toBeVisible()
  })

  const newTitle = `${title} (modifiée)`
  await test.step('the author edits the post', async () => {
    await page.goto(`/equipes/${team.slug}/articles/${postSlug}`)
    await expect(main.getByRole('heading', { level: 2, name: title })).toBeVisible()
    const edit = main.getByRole('link', { name: 'Modifier' })
    await hydrated(edit)
    await edit.click()
    await expect(
      main.getByRole('heading', { level: 1, name: 'Modifier la publication' })
    ).toBeVisible()

    const titleInput = main.getByRole('textbox', { name: 'Titre' })
    await expect(titleInput).toHaveValue(title)
    await hydrated(titleInput)
    await titleInput.fill(newTitle)
    // The saved body comes back into the editor, image included.
    const editor = richText(main)
    await expect(editor.locator('h2')).toHaveText('Au programme')
    await expectImageLoaded(editor.getByRole('img', { name: IMAGE_ALT }))
    // The heading is short enough not to wrap on a phone, so End is the end of its text.
    await editor.locator('h2').click()
    await page.keyboard.press('End')
    await page.keyboard.type(' du dimanche')
    await expect(editor.locator('h2')).toHaveText('Au programme du dimanche')

    await main.getByRole('button', { name: 'Enregistrer' }).click()
    await expect(page.getByText('Publication mise à jour avec succès')).toBeVisible()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/articles/${postSlug}$`))
    await expect(main.getByRole('heading', { level: 2, name: newTitle })).toBeVisible()
    await expect(
      main.getByRole('heading', { level: 2, name: 'Au programme du dimanche', exact: true })
    ).toBeVisible()
    // Nothing else was lost on the way.
    await expect(main.locator('strong', { hasText: 'à 8 h précises' })).toBeVisible()
    await expect(main.getByRole('listitem')).toHaveText(['Casque', 'Gilet jaune'])
    await expect(main.getByRole('link', { name: 'Voir le parcours' })).toHaveAttribute(
      'href',
      LINK_URL
    )
    await expectImageLoaded(main.getByRole('img', { name: IMAGE_ALT }))

    const post = await fetchPost(author, team.slug, postSlug)
    expect(post.name).toBe(newTitle)
    expect(post.media.markdown).toMatch(/^## Au programme du dimanche$/m)
    expect(post.media.markdown).toContain('Rendez-vous **à 8 h précises** devant le club.')
    expect(post.media.markdown).toContain(`[Voir le parcours](${LINK_URL})`)
    expect(post.media.assets.images).toHaveLength(1)
    expect(post.status).toBe('PUBLISHED')
  })

  await test.step("the author deletes the member's comment", async () => {
    await expect(main.getByRole('heading', { name: 'Commentaires (1)' })).toBeVisible()
    const remove = main.getByRole('button', { name: 'Supprimer', exact: true })
    await hydrated(remove)
    await remove.click()
    const dialog = page.getByRole('dialog', { name: 'Supprimer le commentaire' })
    await dialog.getByRole('button', { name: 'Supprimer' }).click()
    await expect(dialog).toBeHidden()
    await expect(main.getByRole('heading', { name: 'Commentaires (0)' })).toBeVisible()
    await expect(main.getByText('Je viens, avec le gilet !')).toHaveCount(0)

    const comments = await apiGet<CommentListResponse>(
      author,
      `/api/teams/${team.slug}/posts/${postSlug}/comments`
    )
    expect(comments.total).toBe(0)
  })

  await test.step('the author deletes the post', async () => {
    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Supprimer' }).click()
    const dialog = page.getByRole('dialog', { name: 'Supprimer' })
    await expect(
      dialog.getByText('Voulez-vous vraiment supprimer cette publication ?', { exact: false })
    ).toBeVisible()
    await dialog.getByRole('button', { name: 'Supprimer' }).click()

    await expect(page.getByText('Publication supprimée avec succès')).toBeVisible()
    await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}$`))
    // A team admin still sees it in the feed, flagged — deletion is soft, and restorable.
    await expect(feedCard(newTitle).getByText('Supprimé', { exact: true })).toBeVisible()
    expect((await fetchPost(author, team.slug, postSlug)).deleted).toBe(true)
  })

  await test.step('for the member it is gone', async () => {
    const { page: memberPage } = reader
    await memberPage.goto('/')
    await expect(memberMain.getByRole('heading', { name: 'Dernières publications' })).toBeVisible()
    // The member's only team has nothing else: its feed is loaded, and empty.
    await expect(memberMain.getByText('Aucune publication pour le moment')).toBeVisible()
    await expect(feedCard(newTitle, memberMain)).toHaveCount(0)

    await memberPage.goto(`/equipes/${team.slug}/articles/${postSlug}`)
    // The page retries the 404 three times before saying so (defect pinned in flow-trips.e2e.ts).
    await expect(memberMain.getByRole('heading', { name: 'Publication introuvable' })).toBeVisible({
      timeout: 15_000,
    })
    expect(await findPost(member, team.slug, postSlug), 'the API answers 404').toBeNull()
  })
  await reader.context.close()
})

test('a post saved as a draft is hidden from members until it is published from its page', async ({
  page,
  browser,
}) => {
  const author = await newUser(unique('Autrice'))
  const member = await newUser(unique('Lectrice'))
  // Rides off: the feed's create button then opens the post editor directly.
  const team = await newTeam(author, unique('Brouillons'), {
    addMemberAllowed: true,
    enableRides: false,
  })
  await addMember(author, team.slug, member)
  const title = unique('Compte rendu à relire')
  const body = 'Merci à tous pour la sortie de dimanche.'
  const main = page.getByRole('main')

  await signIn(page.context(), author)
  await page.goto(`/equipes/${team.slug}`)
  await expect(main.getByRole('heading', { name: "Fil d'actualités" })).toBeVisible()
  const create = main.getByRole('link', { name: 'Nouvelle publication' })
  await hydrated(create)
  await create.click()
  await expect(main.getByRole('heading', { level: 1, name: 'Nouvelle publication' })).toBeVisible()
  const titleInput = main.getByRole('textbox', { name: 'Titre' })
  await hydrated(titleInput)
  await titleInput.fill(title)
  await typeRichText(main, body)
  // A draft is the editor's default.
  await expect(main.getByRole('radio', { name: 'Brouillon' })).toBeChecked()
  await main.getByRole('button', { name: 'Créer la publication' }).click()
  await expect(toasts(page).filter({ hasText: 'Publication créée avec succès' })).toBeVisible()
  await expect(page).toHaveURL(new RegExp(`/equipes/${team.slug}/articles/[^/]+$`))
  const postSlug = new URL(page.url()).pathname.split('/').pop()!
  await expect(main.getByRole('heading', { level: 2, name: title })).toBeVisible()
  await expect(main.getByText('Brouillon', { exact: true })).toBeVisible()
  expect((await fetchPost(author, team.slug, postSlug)).status).toBe('DRAFT')

  const reader = await pageAs(browser, member)
  const memberMain = reader.page.getByRole('main')
  const openMemberFeed = async () => {
    await reader.page.goto(`/equipes/${team.slug}`)
    await expect(memberMain.getByRole('heading', { name: "Fil d'actualités" })).toBeVisible()
    await expect(memberMain.getByText(/^\d+ publications?$/)).toBeVisible()
  }
  try {
    // --- A draft: members see nothing of it.
    expect(await findPost(member, team.slug, postSlug), 'a draft is 404 to a member').toBeNull()
    await openMemberFeed()
    await expect(memberMain.getByText('0 publication', { exact: true })).toBeVisible()
    await expect(entityCard(memberMain, title)).toHaveCount(0)

    // --- Published from the post page's menu.
    const menu = await openActionsMenu(page)
    await menu.getByRole('menuitem', { name: 'Publier' }).click()
    await expect(
      toasts(page).filter({ hasText: 'Publication mise à jour avec succès' })
    ).toBeVisible()
    await expect(main.getByText('Publié', { exact: true })).toBeVisible()
    await expect(main.getByText('Brouillon', { exact: true })).toHaveCount(0)
    const published = await fetchPost(author, team.slug, postSlug)
    expect(published.status).toBe('PUBLISHED')
    expect(published.name).toBe(title)
    expect(published.media.markdown.trim()).toBe(body)

    // --- The member finds it in the feed and reads it.
    await openMemberFeed()
    const card = entityCard(memberMain, title)
    await expect(card).toBeVisible()
    await hydrated(card)
    await card.click()
    await expect(reader.page).toHaveURL(new RegExp(`/equipes/${team.slug}/articles/${postSlug}$`))
    await expect(memberMain.getByRole('heading', { level: 2, name: title })).toBeVisible()
    await expect(memberMain.getByText(body, { exact: true })).toBeVisible()
  } finally {
    await reader.context.close()
  }
})

test.describe('regressions', () => {
  test('the words typed right before saving a post are saved', async ({ page }) => {
    // MarkdownEditor handed the text to the form through a 150 ms debounce that submitting never
    // flushed: words typed within 150 ms of saving were lost, in every form using the editor
    // (fixed 2026-09-25: flushed on blur and on unmount).
    const author = await newUser(unique('Autrice'))
    const team = await newTeam(author, unique('Publications pressées'))
    const post = await newPost(author, team.slug, unique('Publication pressée'), {
      media: markdownMedia('Premier jet.'),
    })
    await signIn(page.context(), author)
    await page.clock.install()
    await page.goto(`/equipes/${team.slug}/articles/${post.slug}/modifier`)
    const main = page.getByRole('main')
    await expect(main.getByRole('textbox', { name: 'Titre' })).toHaveValue(post.name)
    const save = main.getByRole('button', { name: 'Enregistrer' })
    await hydrated(save)
    const editor = richText(main)
    await expect(editor).toHaveText('Premier jet.')

    // The author types the last words and clicks « Enregistrer » straight away: the clock stands
    // still, so the click lands inside the debounce whatever the machine's speed.
    await editor.click()
    await page.keyboard.press('ControlOrMeta+End')
    await page.clock.pauseAt(Date.now() + 60_000)
    await page.keyboard.type(' Relu.')
    await expect(editor, 'the editor shows the typed words').toHaveText('Premier jet. Relu.')
    const saved = page.waitForResponse(
      (r) => r.request().method() === 'PUT' && r.url().endsWith(`/posts/${post.slug}`)
    )
    await save.click()
    expect((await saved).ok(), 'the update was accepted').toBe(true)
    await page.clock.resume()
    await expect(main.getByRole('heading', { level: 2, name: post.name })).toBeVisible()
    await expect(main.getByText('Premier jet. Relu.', { exact: true })).toBeVisible()
    expect((await fetchPost(author, team.slug, post.slug)).media.markdown).toContain(
      'Premier jet. Relu.'
    )
  })
})
