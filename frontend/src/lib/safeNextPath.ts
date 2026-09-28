/** An origin no request ever has: only what resolves against it without leaving it is a path. */
const BASE = 'http://next.invalid'

/**
 * A `?next=` value turned into a same-origin path, or null — never a way off the site.
 *
 * Checked the way the browser will read it, not by prefix: the URL parser drops tabs and newlines
 * and reads `\` as `/`, so `/\t/evil.example` or `/\evil.example` pass a "starts with `/`, not
 * `//`" test and still land on another origin once handed to `location.assign`. What is returned
 * is the parsed path, not the raw value.
 */
export function safeNextPath(value: string | null): string | null {
  if (!value || !value.startsWith('/')) {
    return null
  }
  let url: URL
  try {
    url = new URL(value, BASE)
  } catch {
    return null
  }
  if (url.origin !== BASE) {
    return null
  }
  return url.pathname + url.search + url.hash
}
