import { expect, type BrowserContext, type Page } from '@playwright/test'
import { onHost } from './domains'

/**
 * Helpers for the installable site (pwa.e2e.ts): the service worker, the per-domain manifest, a
 * stand-in for Chromium's install prompt, and a watch on the Firebase chunk.
 *
 * A registered service worker outlives its page within a browser context — every test here opens
 * its own context (`pageAs`) rather than sharing one.
 */

/** The storage key `lib/push/webPush.ts` keeps this browser's FCM token under. */
export const WEB_PUSH_TOKEN_KEY = 'pedalons.webPush.token'

/**
 * Waits until the site's worker (`entry-client.tsx` registers `/sw.js` once the page has loaded)
 * controls `page` — `sw.js` claims its clients on activation — and returns its registration's scope.
 */
export async function waitForServiceWorker(page: Page): Promise<string> {
  await page.waitForFunction(() => navigator.serviceWorker?.controller != null, undefined, {
    timeout: 15_000,
  })
  return page.evaluate(async () => {
    const registration = await navigator.serviceWorker.ready
    return registration.scope
  })
}

/** The names of the Cache Storage caches of the page's origin. */
export const cacheNames = (page: Page) => page.evaluate(() => caches.keys())

/** The web app manifest `host` serves, with the headers it came with. */
export const manifestOf = (host: string) =>
  onHost(host, undefined, async (api) => {
    const response = await api.get('/manifest.webmanifest')
    expect(response.status(), `manifest of ${host}`).toBe(200)
    return {
      headers: response.headers(),
      manifest: (await response.json()) as {
        id: string
        name: string
        short_name: string
        start_url: string
        scope: string
        display: string
        icons: { src: string; sizes: string; purpose?: string }[]
      },
    }
  })

/** The `content` of the `<meta name="…">` tags of a document's head. */
export function namedMeta(html: string, name: string): string[] {
  const headEnd = html.indexOf('</head>')
  const head = headEnd < 0 ? html : html.slice(0, headEnd)
  return [...head.matchAll(/<meta\b[^>]*>/g)]
    .map(([tag]) => tag)
    .filter((tag) => tag.includes(`name="${name}"`))
    .map((tag) => /\bcontent="([^"]*)"/.exec(tag)?.[1] ?? '')
}

interface InstallDouble {
  fire: (outcome: 'accepted' | 'dismissed') => void
  prompts: number
}

/**
 * Stands in for Chromium's `beforeinstallprompt`, from before the first script of every document
 * of `page`: the browser's own events are stopped before the app's listener sees them (a real
 * prompt, fired whenever Chromium judges the site installable, would race the test's), and
 * {@link fireInstallPrompt} dispatches a double whose `prompt()` calls {@link promptCount} counts.
 * Call it before `goto`.
 */
export async function installPromptDouble(page: Page) {
  await page.addInitScript(() => {
    const double: InstallDouble = {
      prompts: 0,
      fire(outcome) {
        const event = new Event('beforeinstallprompt', { cancelable: true })
        Object.assign(event, {
          __e2e: true,
          prompt: () => {
            double.prompts += 1
            return Promise.resolve()
          },
          userChoice: Promise.resolve({ outcome }),
        })
        window.dispatchEvent(event)
      },
    }
    ;(window as unknown as { __e2eInstall: InstallDouble }).__e2eInstall = double
    // Registered before the app's (captureInstallPrompt, entry-client.tsx): stopping it here keeps
    // it from every later listener of `window`.
    window.addEventListener('beforeinstallprompt', (event) => {
      if (!(event as Event & { __e2e?: boolean }).__e2e) {
        event.preventDefault()
        event.stopImmediatePropagation()
      }
    })
  })
}

/** Fires the install prompt double — after hydration, as Chromium would once the page is up. */
export const fireInstallPrompt = (page: Page, outcome: 'accepted' | 'dismissed' = 'dismissed') =>
  page.evaluate(
    (answer) => (window as unknown as { __e2eInstall: InstallDouble }).__e2eInstall.fire(answer),
    outcome
  )

/** How many times the app has called the double's `prompt()` in the current document. */
export const promptCount = (page: Page) =>
  page.evaluate(() => (window as unknown as { __e2eInstall: InstallDouble }).__e2eInstall.prompts)

/**
 * Records every request of `context` for the Firebase SDK — its own chunk, `firebase-vendor`
 * (vite.config.ts), or Firebase's own services (installations, FCM registrations) — from now on.
 */
export function watchFirebase(context: BrowserContext): string[] {
  const loaded: string[] = []
  context.on('request', (request) => {
    if (/firebase|fcmregistrations/i.test(request.url())) loaded.push(request.url())
  })
  return loaded
}
