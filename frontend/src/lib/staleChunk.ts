/**
 * A tab opened before a deploy still runs the previous build, whose lazy chunks
 * (`UserProfilePage-<hash>.js`) the new image no longer serves: the next lazy route fails with
 * « Failed to fetch dynamically imported module ». Reloading the page picks up the new build and
 * its chunks — what the member would have to do by hand on the error screen.
 *
 * At most one reload per {@link RELOAD_WINDOW_MS}: if the chunk still fails right after a reload,
 * the problem is not a stale tab (the server or the network is down), and looping would hide it.
 */

const RELOAD_KEY = 'pedalons-stale-chunk-reload'
const RELOAD_WINDOW_MS = 30_000

/** Chrome, Firefox and Safari word it differently; Vite adds its own for CSS. */
const STALE_CHUNK = [
  /Failed to fetch dynamically imported module/,
  /error loading dynamically imported module/,
  /Importing a module script failed/,
  /Unable to preload CSS/,
]

export function isStaleChunkError(error: unknown): boolean {
  const message = error instanceof Error ? error.message : String(error)
  return STALE_CHUNK.some((pattern) => pattern.test(message))
}

/** Reloads the page unless it was already reloaded for this reason moments ago. */
export function reloadForStaleChunk(): boolean {
  try {
    const last = Number(sessionStorage.getItem(RELOAD_KEY) ?? 0)
    if (Date.now() - last < RELOAD_WINDOW_MS) return false
    sessionStorage.setItem(RELOAD_KEY, String(Date.now()))
  } catch {
    // Without sessionStorage there is no loop guard: let the error screen show instead.
    return false
  }
  window.location.reload()
  return true
}

/** Vite emits `vite:preloadError` when a dynamic import or one of its preloads fails. */
export function installStaleChunkReload(): void {
  if (typeof window === 'undefined') return
  window.addEventListener('vite:preloadError', (event) => {
    // preventDefault keeps Vite from rethrowing: the page is going away.
    if (reloadForStaleChunk()) event.preventDefault()
  })
}
