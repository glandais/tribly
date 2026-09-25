import { expect, test, type Browser, type Locator, type Page } from '@playwright/test'
import type { AuthResponse } from './api'
import { signIn } from './data'

/**
 * Browser-side helpers shared by the journeys: waiting for hydration, recording what the page
 * showed or threw away while a retrying assertion was not looking, a second browser for another
 * user, and the locators every screen shares.
 */

/**
 * Waits until React has hydrated `locator`'s element — i.e. its event handlers are attached. The
 * pages are server-rendered, so a control is on screen (and clickable for Playwright) before that,
 * and a click or keystroke in that window is lost. React marks every node it adopts with a
 * `__reactProps$…` key; that is the signal.
 */
export async function hydrated(locator: Locator) {
  await expect
    .poll(
      () =>
        locator.evaluate((element) =>
          Object.keys(element).some((key) => key.startsWith('__reactProps'))
        ),
      { timeout: 15_000 }
    )
    .toBe(true)
}

/**
 * Waits until the app has taken over the server markup (React's container key on #root) and
 * settled its first fetches.
 */
export async function pageHydrated(page: Page) {
  await page.waitForFunction(() => {
    const root = document.getElementById('root')
    return !!root && Object.keys(root).some((key) => key.startsWith('__reactContainer'))
  })
  await page.waitForLoadState('networkidle')
}

/**
 * Records, from before the first byte is parsed, every console `[hydration]` error (first line),
 * every uncaught page error, and every DOM removal of a node holding one of `markers` — a mismatch
 * makes React throw the server subtree away and client-render it, which is exactly the flicker to
 * rule out. Call it before `goto`.
 */
export async function watchHydration(page: Page, markers: string[] = []) {
  const hydrationErrors: string[] = []
  const pageErrors: string[] = []
  page.on('pageerror', (error) => pageErrors.push(error.message))
  page.on('console', (message) => {
    if (message.type() === 'error' && message.text().includes('[hydration]'))
      hydrationErrors.push(message.text().split('\n')[0])
  })
  await page.addInitScript((watched: string[]) => {
    const removed: string[] = []
    ;(window as unknown as { __e2eRemoved: string[] }).__e2eRemoved = removed
    new MutationObserver((records) => {
      for (const record of records)
        for (const node of record.removedNodes)
          for (const marker of watched) if (node.textContent?.includes(marker)) removed.push(marker)
    }).observe(document, { childList: true, subtree: true })
  }, markers)
  return {
    hydrationErrors,
    pageErrors,
    removed: () =>
      page.evaluate(() => (window as unknown as { __e2eRemoved: string[] }).__e2eRemoved),
  }
}

/** The global toasts (Mantine Notifications) — apiClient shows one for every coded API error. */
export const toasts = (page: Page) => page.locator('.mantine-Notification-root')

/**
 * Records the text of every global toast mounted from now on, on the current document; returns a
 * reader. Use it to prove *no* toast was shown: a retrying `toHaveCount(0)` passes vacuously, since
 * it simply waits for the toast to auto-close (4 s) within its own 5 s timeout.
 */
export async function watchToasts(page: Page): Promise<() => Promise<string[]>> {
  await page.evaluate(() => {
    const w = window as unknown as { __toastTexts: string[] }
    w.__toastTexts = []
    const record = (node: Node) => {
      if (!(node instanceof HTMLElement)) return
      const found = node.matches('.mantine-Notification-root')
        ? [node]
        : Array.from(node.querySelectorAll<HTMLElement>('.mantine-Notification-root'))
      for (const toast of found) w.__toastTexts.push(toast.textContent ?? '')
    }
    new MutationObserver((mutations) => {
      for (const m of mutations) m.addedNodes.forEach(record)
    }).observe(document.body, { childList: true, subtree: true })
  })
  return () => page.evaluate(() => (window as unknown as { __toastTexts: string[] }).__toastTexts)
}

/** `text` with every RegExp metacharacter escaped, to match it literally. */
export const escapeRegExp = (text: string) => text.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')

/** A RegExp matching `text` literally at the start of a name. */
export const startsWith = (text: string) => new RegExp(`^${escapeRegExp(text)}`)

/**
 * The card of an entity in a list or a feed (a ride, a post, a route, a team, an ad): every one is a
 * link holding the entity's name. Names are unique per test, so a second match is a strict-mode
 * error, not a guess.
 */
export const entityCard = (scope: Locator, name: string) =>
  scope.getByRole('link').filter({ hasText: name })

/**
 * The chevron that opens a detail page's other actions (publish, cancel, delete…), grouped with
 * « Modifier ». It holds only an icon and has no accessible name — the defect is pinned once, in
 * flow-rides.e2e.ts — so it is found as the one button of that group.
 */
export const actionsMenu = (page: Page) =>
  page
    .getByRole('main')
    .getByRole('group')
    .filter({ has: page.getByRole('link', { name: 'Modifier' }) })
    .getByRole('button')

/** Opens the actions menu of a detail page, once hydrated; returns the menu. */
export async function openActionsMenu(page: Page) {
  const chevron = actionsMenu(page)
  await hydrated(chevron)
  await chevron.click()
  return page.getByRole('menu')
}

/**
 * A second browser, signed in as `auth` (anonymous when undefined), with the device, locale and
 * time zone of the running test's project — a bare `browser.newContext()` would fall back to a
 * desktop viewport on the mobile project. Close its `context` when done.
 */
export async function pageAs(browser: Browser, auth: AuthResponse | undefined) {
  const { viewport, userAgent, deviceScaleFactor, isMobile, hasTouch, locale, timezoneId } =
    test.info().project.use
  const context = await browser.newContext({
    viewport,
    userAgent,
    deviceScaleFactor,
    isMobile,
    hasTouch,
    locale,
    timezoneId,
  })
  if (auth) await signIn(context, auth)
  return { context, page: await context.newPage() }
}
