import { expect, test, type Page } from '@playwright/test'
import { ssrOutlet } from './ssr'
import { hydrated, pageHydrated } from './ui'

/**
 * The list pages whose search, filters and page live in the query string (frontend/docs/URL_FILTERS.md):
 * what the server rendered for a URL, what the browser fetched afterwards, and the pagination
 * control on both layouts.
 */

/**
 * Every request the browser sends to the list endpoint `endpoint` (a pathname, e.g.
 * `/api/teams/x/routes`) from now on, as its query parameters. The server-side prefetch goes from
 * the SSR server to the backend and never shows here, so on a freshly loaded page anything in this
 * array is a list read the client redid itself. Call it before `goto`.
 */
export function watchListReads(page: Page, endpoint: string): URLSearchParams[] {
  const reads: URLSearchParams[] = []
  page.on('request', (request) => {
    const url = new URL(request.url())
    if (url.pathname === endpoint) reads.push(url.searchParams)
  })
  return reads
}

/**
 * Opens `path` in the browser and returns the server-rendered markup of the document it received
 * (the React outlet, as a crawler reads it), once the app has hydrated and settled — plus the list
 * reads the browser sent meanwhile, which must be none: the route's prefetch put the window
 * `usePaginatedQuery` reads (this page, the next, the previous) in the dehydrated cache, under the
 * very key the page builds from the same URL (frontend/docs/SSR-data-loading.md).
 */
export async function openServerRendered(page: Page, path: string, endpoint: string) {
  const reads = watchListReads(page, endpoint)
  const response = await page.goto(path)
  expect(response?.status(), `GET ${path}`).toBe(200)
  const markup = ssrOutlet(await response!.text())
  await pageHydrated(page)
  return { markup, reads }
}

/** The list reads as `key=value&…` strings, for a readable failure message. */
export const readsOf = (reads: URLSearchParams[]) => reads.map((params) => params.toString())

/**
 * Asserts that `names` all appear in `markup`, in that order, and that none of `absent` does. Names
 * are the tests' `unique()` names: no markup escaping to account for.
 */
export function expectInMarkup(markup: string, names: string[], absent: string[] = []) {
  const positions = names.map((name) => markup.indexOf(name))
  for (const [i, name] of names.entries())
    expect(positions[i], `the server markup lists « ${name} »`).toBeGreaterThanOrEqual(0)
  expect(positions, 'the server markup lists them in that order').toEqual(
    [...positions].sort((a, b) => a - b)
  )
  for (const name of absent)
    expect(markup, `the server markup does not list « ${name} »`).not.toContain(name)
}

/** The page's query string as an object — the filters the page wrote to its URL. */
export const queryOf = (page: Page) => Object.fromEntries(new URL(page.url()).searchParams)

/** Waits for the page to have written exactly `expected` to its query string. */
export const expectQuery = (page: Page, expected: Record<string, string>) =>
  expect.poll(() => queryOf(page)).toEqual(expected)

const onMobile = () => !!test.info().project.use.isMobile

/**
 * Goes to the next page with the pagination control's « Page suivante » arrow — named on both
 * layouts (the desktop arrows only got an accessible name with db15941e).
 */
export async function nextPage(page: Page) {
  const control = page
    .getByRole('navigation', { name: 'Pagination' })
    .getByRole('button', { name: 'Page suivante', exact: true })
  await hydrated(control)
  await control.click()
}

/** The pagination control shows page `current` (1-based) of `total`. */
export async function expectCurrentPage(page: Page, current: number, total: number) {
  const nav = page.getByRole('navigation', { name: 'Pagination' })
  if (onMobile()) {
    await expect(nav).toContainText(`Page ${current} sur ${total}`)
  } else {
    await expect(
      nav.getByRole('button', { name: `Aller à la page ${current}`, exact: true })
    ).toHaveAttribute('data-active', 'true')
    await expect(
      nav.getByRole('button', { name: `Aller à la page ${total}`, exact: true })
    ).toBeVisible()
  }
}
